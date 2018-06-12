package com.stouduo.mesh;

import com.stouduo.mesh.registry.IRegistry;
import com.stouduo.mesh.registry.impl.EtcdRegistry;
import com.stouduo.mesh.rpc.loadbalance.strategy.ILbStrategy;
import com.stouduo.mesh.rpc.loadbalance.strategy.impl.*;
import com.stouduo.mesh.server.AgentClient;
import com.stouduo.mesh.server.ProviderAgentClient;
import com.stouduo.mesh.server.invoke.ConsumerInvokeHandler;
import com.stouduo.mesh.server.invoke.Invoker;
import com.stouduo.mesh.server.invoke.ProviderInvokeHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgentConfiguration {

//    @Bean
//    public ApplicationRunner serverStart() {
//        return new AgentServer();
//    }

    @Bean
    @ConditionalOnProperty(value = "type", havingValue = "provider")
    public Invoker providerInvoker() {
        return new ProviderInvokeHandler();
    }

    @Bean
    @ConditionalOnProperty(value = "type", havingValue = "consumer")
    public Invoker consumerInvoker() {
        return new ConsumerInvokeHandler();
    }

    @Bean
    @ConditionalOnMissingBean(AgentClient.class)
//    @ConditionalOnProperty(value = "type", havingValue = "provider")
    public AgentClient providerAgentClient(@Value("${dubbo.protocol.port:4321}") int port, @Value("${agent.client.pool.maxChannels:16}") int maxChannels) {
        return new ProviderAgentClient(port, maxChannels);
    }

//    @Bean
//    @ConditionalOnMissingBean(AgentClient.class)
//    @ConditionalOnProperty(value = "type", havingValue = "consumer")
//    public AgentClient comsumerAgentClient(@Value("${agent.client.pool.maxChannels:32}") int maxChannels) {
//        return new ConsumerAgentClient(maxChannels);
//    }

//    @Bean
//    @ConditionalOnProperty(value = "type", havingValue = "provider")
//    public ChannelInitializer providerServerChannelInitializer() {
//        return new ProviderServerChannelInitializer();
//    }

//    @Bean
//    @ConditionalOnProperty(value = "type", havingValue = "provider")
//    public ChannelPoolHandler providerClientChannelPoolHandler() {
//        return new ProviderClientChannelPoolHandler();
//    }

//    @Bean
//    @ConditionalOnProperty(value = "type", havingValue = "consumer")
//    public ChannelInitializer consumerServerChannelInitializer() {
//        return new ConsumerServerChannelInitializer();
//    }
//
//    @Bean
//    @ConditionalOnProperty(value = "type", havingValue = "consumer")
//    public ChannelPoolHandler consumerClientChannelPoolHandler() {
//        return new ConsumerClientChannelPoolHandler();
//    }

    @Bean
    @ConditionalOnMissingBean(IRegistry.class)
    public IRegistry etcdRegistry() {
        return new EtcdRegistry();
    }

    @Bean
    @ConditionalOnProperty(value = "agent.loadbalance.strategy", havingValue = "weightRound")
    public ILbStrategy weightRoundLbStrategy() {
        return new WeightRoundLbStrategy();
    }

    @Bean
    @ConditionalOnProperty(value = "agent.loadbalance.strategy", havingValue = "wrr")
    public ILbStrategy wrrLbStrategy() {
        return new WrrLbStrategy();
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
