package com.stouduo.mesh.invokehandler;

import org.springframework.web.reactive.function.server.ServerRequest;

public interface InvokeHandler {
    Object invoke(ServerRequest request);
}
