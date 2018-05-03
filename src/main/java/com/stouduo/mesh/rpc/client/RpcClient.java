package com.stouduo.mesh.rpc.client;


import org.springframework.web.reactive.function.server.ServerRequest;

public interface RpcClient {
    Object invoke(ServerRequest request) throws Exception;
}
