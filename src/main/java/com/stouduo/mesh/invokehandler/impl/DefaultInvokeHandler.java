package com.stouduo.mesh.invokehandler.impl;

import com.stouduo.mesh.invokehandler.InvokeHandler;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

public class DefaultInvokeHandler implements InvokeHandler {
    @Override
    public Mono invoke(ServerRequest request) {
        return Mono.justOrEmpty("请指定当前服务的类型（consumer 或者 provider）");
    }
}
