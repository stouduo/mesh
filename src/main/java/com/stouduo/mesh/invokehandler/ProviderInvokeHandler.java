package com.stouduo.mesh.invokehandler;

import org.springframework.web.reactive.function.server.ServerRequest;

public class ProviderInvokeHandler implements InvokeHandler {
    @Override
    public Object invoke(ServerRequest request) {
        return "provider";
    }
}
