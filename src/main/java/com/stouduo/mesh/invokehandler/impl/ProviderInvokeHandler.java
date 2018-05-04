package com.stouduo.mesh.invokehandler.impl;

import com.stouduo.mesh.invokehandler.InvokeHandler;
import com.stouduo.mesh.rpc.client.RpcClient;
import com.stouduo.mesh.rpc.client.RpcRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.server.ServerRequest;

public class ProviderInvokeHandler implements InvokeHandler {
    @Autowired
    private RpcClient rpcClient;

    @Override
    public Object invoke(ServerRequest request) throws Exception {
        return rpcClient.invoke(new RpcRequest().setParameters(request.attributes()));
    }
}
