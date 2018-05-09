package com.stouduo.mesh.invokehandler.impl;

import com.stouduo.mesh.invokehandler.InvokeHandler;
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
import java.util.Map;
import java.util.function.Function;

public class ConsumerInvokeHandler implements InvokeHandler {
    private Logger logger = LoggerFactory.getLogger(ConsumerInvokeHandler.class);

    @Autowired
    private ILbStrategy iLbStrategy;
    @Autowired
    private IRegistry iRegistry;
    @Autowired
    private AgentRpcClient agentRpcClient;
    @Value("${agent.consumer.reqParam.serverName}")
    private String serverParamName;
    @Value("${agent.consumer.retry:3}")
    private long retry;

    @Override
    public Mono invoke(ServerRequest request) {
        return request.formData().flatMap(map -> {
            try {
                Map<String, String> params = map.toSingleValueMap();
                Endpoint endpoint = iLbStrategy.lbStrategy(iRegistry.find(params.get(serverParamName)));
                return doInvoke(endpoint, map).retry(retry, (e) -> e instanceof ConnectTimeoutException).onErrorResume(ConnectException.class, o -> null);
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
}
