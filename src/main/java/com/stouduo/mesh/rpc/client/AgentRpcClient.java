package com.stouduo.mesh.rpc.client;

import reactor.core.publisher.Mono;

public interface AgentRpcClient {

    Mono invoke(RpcRequest request) throws Exception;

    default String getProtocol() {
        return "http://";
    }
}
