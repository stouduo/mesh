package com.stouduo.mesh;

import com.stouduo.mesh.dubbo.modify.CustomConsumerRpcClient;
import com.stouduo.mesh.invokehandler.InvokeHandler;
import com.stouduo.mesh.invokehandler.impl.ConsumerInvokeHandler;
import com.stouduo.mesh.invokehandler.impl.DefaultInvokeHandler;
import com.stouduo.mesh.invokehandler.impl.ProviderInvokeHandler;
import com.stouduo.mesh.registry.IRegistry;
import com.stouduo.mesh.registry.impl.EtcdRegistry;
import com.stouduo.mesh.rpc.client.AgentRpcClient;
import com.stouduo.mesh.rpc.client.ConsumerRpcClient;
import com.stouduo.mesh.rpc.client.impl.DefaultAgentRpcClient;
import com.stouduo.mesh.rpc.loadbalance.strategy.ILbStrategy;
import com.stouduo.mesh.rpc.loadbalance.strategy.impl.DefaultLbStrategy;
import com.stouduo.mesh.rpc.loadbalance.strategy.impl.RoundLbStrategy;
import com.stouduo.mesh.rpc.loadbalance.strategy.impl.WeightRandomLbStrategy;
import com.stouduo.mesh.rpc.loadbalance.strategy.impl.WeightRoundLbStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgentConfiguration {

    @Bean
    @ConditionalOnProperty(value = "type", havingValue = "consumer")
    public InvokeHandler consumerInvokerHandler() {
        return new ConsumerInvokeHandler();
    }

    @Bean
    @ConditionalOnProperty(value = "type", havingValue = "provider")
    public InvokeHandler providerInvokerHandler() {
        return new ProviderInvokeHandler();
    }

    @Bean
    @ConditionalOnMissingBean(InvokeHandler.class)
    public InvokeHandler defaultInvokerHandler() {
        return new DefaultInvokeHandler();
    }

    @Bean
    @ConditionalOnMissingBean(IRegistry.class)
    public IRegistry etcdRegistry() {
        return new EtcdRegistry();
    }

    @Bean
    @ConditionalOnMissingBean(ConsumerRpcClient.class)
    public ConsumerRpcClient dubboRpcClient(@Value("${dubbo.protocol.port:20880}") int dubboProtoPort, @Value("${agent.provider.maxChannels:4}") int maxChannels) {
        return new CustomConsumerRpcClient(dubboProtoPort, maxChannels);
    }
//    @Bean
//    @ConditionalOnMissingBean(ConsumerRpcClient.class)
//    public ConsumerRpcClient dubboRpcClient(@Value("${dubbo.protocol.port:20880}") int dubboProtoPort) {
//        return new DubboConsumerRpcClient().setConnectManager(new ConnecManager(dubboProtoPort));
//    }


    @Bean
    @ConditionalOnMissingBean(AgentRpcClient.class)
    public AgentRpcClient agentRpcClient() {
        return new DefaultAgentRpcClient();
    }

    @Bean
    @ConditionalOnProperty(value = "agent.loadbalance.strategy", havingValue = "weightRound")
    public ILbStrategy weightRoundLbStrategy() {
        return new WeightRoundLbStrategy();
    }

    @Bean
    @ConditionalOnProperty(value = "agent.loadbalance.strategy", havingValue = "round")
    public ILbStrategy roundLbStrategy() {
        return new RoundLbStrategy();
    }

    @Bean
    @ConditionalOnProperty(value = "agent.loadbalance.strategy", havingValue = "weightRandom")
    public ILbStrategy weightRandomLbStrategy() {
        return new WeightRandomLbStrategy();
    }

    @Bean
    @ConditionalOnMissingBean(ILbStrategy.class)
    public ILbStrategy defaultLbStrategy() {
        return new DefaultLbStrategy();
    }
}
