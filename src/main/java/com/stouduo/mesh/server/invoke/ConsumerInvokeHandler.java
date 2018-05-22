package com.stouduo.mesh.server.invoke;

import com.stouduo.mesh.registry.IRegistry;
import com.stouduo.mesh.rpc.RpcRequest;
import com.stouduo.mesh.rpc.loadbalance.strategy.ILbStrategy;
import com.stouduo.mesh.server.AgentClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConsumerInvokeHandler {
    private static Logger logger = LoggerFactory.getLogger(ConsumerInvokeHandler.class);

    @Autowired
    private ILbStrategy iLbStrategy;
    @Autowired
    private IRegistry iRegistry;
    @Autowired
    private AgentClient agentClient;
    @Value("${agent.consumer.reqParam.serverName}")
    private String serverParamName;
    @Value("${agent.consumer.retry:3}")
    private long retry;

    public Object invoke(RpcRequest request) {
        try {
            request.setRemoteServer(iLbStrategy.lbStrategy(iRegistry.find(request.getParameterStr(serverParamName))));
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        Object ret = agentClient.invoke(request);
        return ret;
    }
}
