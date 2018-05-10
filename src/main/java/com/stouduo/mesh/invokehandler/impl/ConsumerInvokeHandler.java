package com.stouduo.mesh.invokehandler.impl;

import com.stouduo.mesh.invokehandler.InvokeHandler;
import com.stouduo.mesh.registry.BaseRegistry;
import com.stouduo.mesh.registry.IRegistry;
import com.stouduo.mesh.rpc.client.AgentRpcClient;
import com.stouduo.mesh.rpc.client.RpcRequest;
import com.stouduo.mesh.rpc.loadbalance.strategy.ILbStrategy;
import com.stouduo.mesh.util.Endpoint;
import io.netty.channel.ConnectTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

public class ConsumerInvokeHandler implements InvokeHandler, AutoCloseable {
    private Logger logger = LoggerFactory.getLogger(ConsumerInvokeHandler.class);

    @Autowired
    private ILbStrategy iLbStrategy;
    @Autowired
    private IRegistry iRegistry;
    @Autowired
    private BaseRegistry baseRegistry;
    @Autowired
    private AgentRpcClient agentRpcClient;
    @Value("${agent.consumer.reqParam.serverName}")
    private String serverParamName;
    @Value("${agent.consumer.retry:3}")
    private long retry;

    @Value("${agent.provider.healthCheck.params}")
    private Map<String, String> params;
    private boolean stopHealthCheck = false;

    @Override
    public Mono invoke(ServerRequest request) {
        return request.formData().flatMap(map -> {
            try {
                Map<String, String> params = map.toSingleValueMap();
                final Endpoint endpoint = iLbStrategy.lbStrategy(iRegistry.find(params.get(serverParamName)));
                return doInvoke(endpoint, map).retry(retry, e -> e instanceof ConnectTimeoutException).onErrorResume(ConnectException.class, o -> {
                    try {
                        if (((ConnectException) o).getMessage().contains("Connection refused")) {
                            if (endpoint != null) {
                                iRegistry.serverDown(endpoint);
                                Thread.sleep(10);
                                Endpoint recallEndPoint = iLbStrategy.lbStrategy(iRegistry.find(params.get(serverParamName)));
                                if (endpoint.equals(recallEndPoint)) return Mono.just("抱歉，已没有可用的服务！");
                                return doInvoke(recallEndPoint, map);
                            }
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                    return Mono.empty();
                });
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
            return null;
        });
    }

    private Mono doInvoke(Endpoint endpoint, MultiValueMap<String, String> map) throws Exception {
        String remoteUrl = endpoint.getHost() + ":" + endpoint.getPort();
        logger.info(">>>>>调用服务地址为：" + remoteUrl);
        return agentRpcClient.invoke(new RpcRequest(remoteUrl).setMultiParameters(map));
    }

    protected void HealthCheck() {
        Map<String, List<Endpoint>> providers = baseRegistry.getProviders();
        ExecutorService reRegThreadPool = Executors.newFixedThreadPool(3);
        Executors.newSingleThreadExecutor().submit(
                () -> {
                    try {
                        long retry = 0;
                        while (!stopHealthCheck) {
                            while (providers != null && providers.size() > 0)
                                providers.forEach((k, v) -> {
                                    v.forEach(endpoint -> {
                                        try {
                                            agentRpcClient.invoke(new RpcRequest().setParameters(params)).retry(retry, e -> e instanceof ConnectTimeoutException).onErrorResume(ConnectException.class, o -> {
                                                if (((ConnectException) o).getMessage().contains("Connection refused")) {
                                                    try {
                                                        iRegistry.serverDown(endpoint);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                } else {
                                                    reRegThreadPool.submit(() -> {
                                                        while (!stopHealthCheck) {
                                                            try {
                                                                Thread.sleep(10000);
                                                                iRegistry.register(k.split("/")[1],);
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                }
                                                return null;
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    });
                                });
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                }
        );
    }

    @Override
    public void close() throws Exception {
        this.stopHealthCheck = true;
    }
}
