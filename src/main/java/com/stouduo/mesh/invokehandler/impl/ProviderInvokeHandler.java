package com.stouduo.mesh.invokehandler.impl;

import com.stouduo.mesh.invokehandler.InvokeHandler;
import com.stouduo.mesh.rpc.client.ConsumerRpcClient;
import com.stouduo.mesh.rpc.client.RpcRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

public class ProviderInvokeHandler implements InvokeHandler {
    @Autowired
    private ConsumerRpcClient consumerRpcClient;

    @Override
    public Mono invoke(ServerRequest request) throws Exception {
        return Mono.justOrEmpty(consumerRpcClient.invoke(new RpcRequest().setParameters(request.formData().toFuture().get().toSingleValueMap())));
    }
}
