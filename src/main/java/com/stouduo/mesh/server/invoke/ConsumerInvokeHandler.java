package com.stouduo.mesh.server.invoke;

import com.stouduo.mesh.dubbo.model.RpcDTO;
import com.stouduo.mesh.registry.IRegistry;
import com.stouduo.mesh.rpc.loadbalance.strategy.ILbStrategy;
import com.stouduo.mesh.server.AgentClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

public class ConsumerInvokeHandler implements Invoker{
    private static Logger logger = LoggerFactory.getLogger(ConsumerInvokeHandler.class);

    @Autowired
    private ILbStrategy iLbStrategy;
    @Autowired
    private IRegistry iRegistry;
    @Autowired
    private AgentClient agentClient;
    @Value("${agent.provider.serviceName}")
    private String serviceName;

    public void invoke(RpcDTO data) {
        try {
            agentClient.invoke(data.setRemoteServer(iLbStrategy.lbStrategy(iRegistry.find(serviceName))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
