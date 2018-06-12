package com.stouduo.mesh.server.invoke;

import com.stouduo.mesh.dubbo.model.RpcDTO;
import com.stouduo.mesh.server.AgentClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class ProviderInvokeHandler implements Invoker{
    @Autowired
    private AgentClient agentClient;

    public void invoke(RpcDTO data) {
        agentClient.invoke(data);
    }
}
