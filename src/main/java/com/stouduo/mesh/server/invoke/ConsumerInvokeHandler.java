package com.stouduo.mesh.server.invoke;

import com.stouduo.mesh.registry.IRegistry;
import com.stouduo.mesh.rpc.client.AgentRpcClient;
import com.stouduo.mesh.rpc.client.RpcRequest;
import com.stouduo.mesh.rpc.loadbalance.strategy.ILbStrategy;
import com.stouduo.mesh.server.AgentClient;
import com.stouduo.mesh.util.Endpoint;
import io.netty.channel.ConnectTimeoutException;
import io.protostuff.Rpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.util.Map;

public class ConsumerInvokeHandler   {
    private Logger logger = LoggerFactory.getLogger(ConsumerInvokeHandler.class);

    @Autowired
    private ILbStrategy iLbStrategy;
    @Autowired
    private IRegistry iRegistry;
    @Autowired
    private AgentClient agentClient;
    @Value("${agent.consumer.reqParam.serverName}")
    private String serverParamName;
    @Value("${agent.consumer.retry:3}")
    private long retry;

    public Object invoke(RpcRequest request) {
        return agentClient.invoke(request);
    }
}
