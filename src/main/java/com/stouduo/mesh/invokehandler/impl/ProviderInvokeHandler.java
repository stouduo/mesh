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
    public Mono invoke(ServerRequest request) {
        return request.formData().flatMap(map -> {
            try {
                return Mono.justOrEmpty(consumerRpcClient.invoke(new RpcRequest().setParameters(map.toSingleValueMap())));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }
}
