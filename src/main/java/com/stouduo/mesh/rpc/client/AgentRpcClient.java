package com.stouduo.mesh.rpc.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

@Component("agentRpc")
public class AgentRpcClient implements RpcClient {

    @Override
    public Object invoke(ServerRequest request) throws Exception {
        return null;
    }
}
