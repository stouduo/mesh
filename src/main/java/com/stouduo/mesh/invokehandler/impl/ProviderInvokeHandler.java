package com.stouduo.mesh.invokehandler.impl;

import com.stouduo.mesh.invokehandler.InvokeHandler;
import com.stouduo.mesh.registry.BaseRegistry;
import com.stouduo.mesh.registry.IRegistry;
import com.stouduo.mesh.rpc.client.ConsumerRpcClient;
import com.stouduo.mesh.rpc.client.RpcRequest;
import com.stouduo.mesh.util.Endpoint;
import com.sun.org.apache.regexp.internal.RE;
import io.netty.channel.ConnectTimeoutException;
import jdk.nashorn.internal.runtime.FindProperty;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.net.ConnectException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ProviderInvokeHandler implements InvokeHandler, AutoCloseable {
    private org.slf4j.Logger logger = LoggerFactory.getLogger(ProviderInvokeHandler.class);
    @Autowired
    private ConsumerRpcClient consumerRpcClient;
    @Autowired
    private IRegistry iRegistry;
    @Value("${agent.consumer.retry:3}")
    private long retry;
    private volatile Map<String, String> params;
    private boolean stopHealthCheck;
    private boolean serverDown;
    private ExecutorService healthCheck;
    private ExecutorService reRegister;
    @Value("${agent.provider.healthCheck.reRegister.failTimeOut:120}")
    private long failTimeOut;
    @Value("${agent.provider.healthCheck.rate:60}")
    private long healthCheckRate;


    @PostConstruct
    private void init() {
        this.healthCheck = Executors.newSingleThreadExecutor();
        this.reRegister = Executors.newSingleThreadExecutor();
        this.stopHealthCheck = false;
        this.serverDown = false;
        HealthCheck();
    }

    @Override
    public Mono invoke(ServerRequest request) {
        return request.formData().flatMap(map -> {
            try {
                if (params == null) {
                    synchronized (ProviderInvokeHandler.this) {
                        if (params == null) {
                            params = map.toSingleValueMap();
                        }
                    }
                }
                return Mono.just(consumerRpcClient.invoke(new RpcRequest().setMultiParameters(map)));
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
            return Mono.empty();
        });

    }

    private void HealthCheck() {
        healthCheck.submit(
                () -> {
                    try {
                        while (!stopHealthCheck) {
                            if (!serverDown)
                                if (params != null && params.size() > 0) {
                                    Mono.just(consumerRpcClient.invoke(new RpcRequest().setParameters(params))).retry(retry, e -> e instanceof ConnectTimeoutException).onErrorResume(ConnectException.class, o -> {
                                        try {
                                            iRegistry.serverDown();
                                        } catch (Exception e) {
                                            logger.error(e.getMessage());
                                        } finally {
                                            serverDown = true;
                                        }
                                        if (o instanceof ConnectTimeoutException) {
                                            reRegister.submit(() -> {
                                                try {
                                                    Thread.sleep(failTimeOut * 1000);
                                                    iRegistry.register();
                                                } catch (Exception e) {
                                                    logger.error(e.getMessage());
                                                } finally {
                                                    serverDown = false;
                                                }
                                            });
                                        }
                                        return Mono.empty();
                                    });
                                    Thread.sleep(healthCheckRate * 1000);
                                }
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
        this.healthCheck.awaitTermination(1, TimeUnit.MINUTES);
        this.reRegister.awaitTermination(1, TimeUnit.MINUTES);
    }
}
