package com.stouduo.mesh.requesthandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterConfig {
    @Autowired
    private RequestHandler requestHandler;

    @Bean
    public RouterFunction<ServerResponse> routerFunction() {
        return route(GET("/").and(accept(MediaType.ALL)), requestHandler::invoke)
                .andRoute(POST("/").and(accept(MediaType.ALL)), requestHandler::invoke);
    }
}