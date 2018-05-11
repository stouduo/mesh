package com.stouduo.mesh.rpc.client.impl;

import com.stouduo.mesh.rpc.client.AgentRpcClient;
import com.stouduo.mesh.rpc.client.RpcRequest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class DefaultAgentRpcClient implements AgentRpcClient {
    private WebClient webClient;

    public DefaultAgentRpcClient() {
        this.webClient = WebClient.create();
    }

    @Override
    public Mono invoke(RpcRequest request) {
        return webClient.post().uri(getProtocol() + request.getRemoteUri()).syncBody(request.getMultiParameters()).
                retrieve().bodyToMono(Object.class);
    }
}
