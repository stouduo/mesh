package com.stouduo.mesh.invokehandler.impl;

import com.stouduo.mesh.invokehandler.InvokeHandler;
import com.stouduo.mesh.registry.IRegistry;
import com.stouduo.mesh.rpc.client.RpcClient;
import com.stouduo.mesh.rpc.client.RpcRequest;
import com.stouduo.mesh.rpc.loadbalance.strategy.ILbStrategy;
import com.stouduo.mesh.util.Endpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.server.ServerRequest;

import javax.annotation.Resource;

public class ConsumerInvokeHandler implements InvokeHandler {

    @Autowired
    private ILbStrategy iLbStrategy;
    @Autowired
    private IRegistry iRegistry;
    @Resource(name = "agentRpc")
    private RpcClient rpcClient;

    @Override
    public Object invoke(ServerRequest request) throws Exception {
        Endpoint endpoint = iLbStrategy.lbStrategy(iRegistry.find(request.queryParam("interface").get()));
        return rpcClient.invoke(new RpcRequest(endpoint.getHost() + ":" + endpoint.getPort()).setParameters(request.attributes()));
    }
}
