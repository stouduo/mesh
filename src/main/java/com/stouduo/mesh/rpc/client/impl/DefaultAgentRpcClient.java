package com.stouduo.mesh.rpc.client.impl;

import com.stouduo.mesh.rpc.client.AgentRpcClient;
import com.stouduo.mesh.rpc.client.RpcRequest;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class DefaultAgentRpcClient implements AgentRpcClient {
    private WebClient webClient;

    public DefaultAgentRpcClient() {
        this.webClient = WebClient.create();
    }

    @Override
    public Mono invoke(RpcRequest request) {
        return webClient.post().uri(getProtocol() + request.getRequsetUrl()).contentType(MediaType.APPLICATION_FORM_URLENCODED).body(Mono.just(request.getParameters()), Object.class).retrieve().bodyToMono(Object.class);
    }
}
