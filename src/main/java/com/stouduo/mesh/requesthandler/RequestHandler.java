package com.stouduo.mesh.requesthandler;

import com.stouduo.mesh.invokehandler.InvokeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class RequestHandler {
    @Autowired
    private InvokeHandler invokeHandler;

    public Mono<ServerResponse> invoke(ServerRequest request) {
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(invokeHandler.invoke(request), Object.class);
    }
}
