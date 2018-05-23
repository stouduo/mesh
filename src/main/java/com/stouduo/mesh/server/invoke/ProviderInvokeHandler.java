package com.stouduo.mesh.server.invoke;

import com.stouduo.mesh.rpc.RpcRequest;
import com.stouduo.mesh.server.AgentClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProviderInvokeHandler {
    @Autowired
    private AgentClient agentClient;

    public void invoke(RpcRequest request) {
        agentClient.invoke(request);
    }
}
