package com.stouduo.mesh.server.invoke;

import com.stouduo.mesh.rpc.client.RpcRequest;
import com.stouduo.mesh.server.AgentClient;
import org.springframework.beans.factory.annotation.Autowired;

public class ProviderInvokeHandler {
    @Autowired
    private AgentClient agentClient;


    public Object invoke(RpcRequest request) {
        return agentClient.invoke(request);
    }
}
