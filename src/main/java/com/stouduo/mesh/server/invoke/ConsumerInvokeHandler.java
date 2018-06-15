package com.stouduo.mesh.server.invoke;

import com.stouduo.mesh.dubbo.model.RpcDTO;
import com.stouduo.mesh.registry.BaseRegistry;
import com.stouduo.mesh.registry.IRegistry;
import com.stouduo.mesh.rpc.loadbalance.strategy.ILbStrategy;
import com.stouduo.mesh.server.AgentClient;
import com.stouduo.mesh.server.netty.consumer.ConsumerAgentClient;
import com.stouduo.mesh.server.netty.provider.ProviderAgentClient;
import com.stouduo.mesh.util.Endpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConsumerInvokeHandler implements Invoker {
    private static Logger logger = LoggerFactory.getLogger(ConsumerInvokeHandler.class);

    @Autowired
    private ILbStrategy iLbStrategy;
    @Autowired
    private IRegistry iRegistry;
    @Value("${agent.client.pool.maxChannels:32}")
    private int maxChannels;
    //    @Autowired
//    private AgentClient agentClient;
    @Value("${agent.provider.serviceName}")
    private String serviceName;

    private static ConcurrentHashMap<Endpoint, ConsumerAgentClient> clients = new ConcurrentHashMap<>();

    public void invoke(RpcDTO data) {
        try {
            Endpoint endpoint = iLbStrategy.lbStrategy(iRegistry.find(serviceName));
            ConsumerAgentClient client;
            synchronized (this) {
                client = clients.get(endpoint);
                if (client == null) {
                    client = new ConsumerAgentClient(maxChannels);
                    client.start();
                    clients.put(endpoint, client);
                }
            }
            client.invoke(data.setRemoteServer(endpoint));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
