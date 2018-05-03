package com.stouduo.mesh.invokehandler;

import org.springframework.web.reactive.function.server.ServerRequest;

public class DefaultInvokeHandler implements InvokeHandler {
    @Override
    public Object invoke(ServerRequest request) {
        return "请指定当前服务的类型（consumer 或者 provider）";
    }
}
