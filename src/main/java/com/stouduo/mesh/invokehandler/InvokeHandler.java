package com.stouduo.mesh.invokehandler;

import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

public interface InvokeHandler {
    Mono invoke(ServerRequest request) throws Exception;
}
