package com.stouduo.mesh.invokehandler.impl;

import com.stouduo.mesh.invokehandler.InvokeHandler;
import com.stouduo.mesh.registry.IRegistry;
import com.stouduo.mesh.rpc.Test;
import com.stouduo.mesh.rpc.client.AgentRpcClient;
import com.stouduo.mesh.rpc.client.ConsumerRpcClient;
import com.stouduo.mesh.rpc.client.RpcRequest;
import com.stouduo.mesh.rpc.loadbalance.strategy.ILbStrategy;
import com.stouduo.mesh.util.Endpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

public class ConsumerInvokeHandler implements InvokeHandler {

    @Autowired
    private ILbStrategy iLbStrategy;
    @Autowired
    private IRegistry iRegistry;
    @Autowired
    private AgentRpcClient agentRpcClient;

    @Override
    public Mono invoke(ServerRequest request) throws Exception {
        Map<String, String> params = request.body(BodyExtractors.toFormData()).toFuture().get().toSingleValueMap();
        Endpoint endpoint = iLbStrategy.lbStrategy(iRegistry.find(params.get("interface")));
        return agentRpcClient.invoke(new RpcRequest(endpoint.getHost() + ":" + endpoint.getPort()).setParameters(params));
    }
}
