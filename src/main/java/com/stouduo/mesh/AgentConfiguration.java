package com.stouduo.mesh;

import com.stouduo.mesh.registry.IRegistry;
import com.stouduo.mesh.registry.impl.EtcdRegistry;
import com.stouduo.mesh.rpc.loadbalance.strategy.ILbStrategy;
import com.stouduo.mesh.rpc.loadbalance.strategy.impl.DefaultLbStrategy;
import com.stouduo.mesh.rpc.loadbalance.strategy.impl.RoundLbStrategy;
import com.stouduo.mesh.rpc.loadbalance.strategy.impl.WeightRandomLbStrategy;
import com.stouduo.mesh.rpc.loadbalance.strategy.impl.WeightRoundLbStrategy;
import com.stouduo.mesh.server.AgentClient;
import com.stouduo.mesh.server.AgentServer;
import com.stouduo.mesh.server.netty.consumer.ConsumerAgentClient;
import com.stouduo.mesh.server.netty.consumer.ConsumerClientChannelPoolHandler;
import com.stouduo.mesh.server.netty.consumer.ConsumerServerChannelInitializer;
import com.stouduo.mesh.server.netty.provider.ProviderAgentClient;
import com.stouduo.mesh.server.netty.provider.ProviderClientChannelPoolHandler;
import com.stouduo.mesh.server.netty.provider.ProviderServerChannelInitializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.pool.ChannelPoolHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgentConfiguration {

    @Bean
    public ApplicationRunner serverStart() {
        return new AgentServer();
    }

    @Bean
    @ConditionalOnMissingBean(AgentClient.class)
    @ConditionalOnProperty(value = "type", havingValue = "provider")
    public AgentClient providerAgentClient(@Value("${dubbo.protocol.port:4321}") int port, @Value("${agent.client.pool.maxChannels:4}") int maxChannels) {
        return new ProviderAgentClient(port, maxChannels);
    }

    @Bean
    @ConditionalOnMissingBean(AgentClient.class)
    @ConditionalOnProperty(value = "type", havingValue = "consumer")
    public AgentClient comsumerAgentClient(@Value("${agent.client.pool.maxChannels:16}") int maxChannels) {
        return new ConsumerAgentClient(maxChannels);
    }

    @Bean
    @ConditionalOnProperty(value = "type", havingValue = "provider")
    public ChannelInitializer providerServerChannelInitializer() {
        return new ProviderServerChannelInitializer();
    }

    @Bean
    @ConditionalOnProperty(value = "type", havingValue = "provider")
    public ChannelPoolHandler providerClientChannelPoolHandler() {
        return new ProviderClientChannelPoolHandler();
    }

    @Bean
    @ConditionalOnProperty(value = "type", havingValue = "consumer")
    public ChannelInitializer consumerServerChannelInitializer() {
        return new ConsumerServerChannelInitializer();
    }

    @Bean
    @ConditionalOnProperty(value = "type", havingValue = "consumer")
    public ChannelPoolHandler consumerClientChannelPoolHandler() {
        return new ConsumerClientChannelPoolHandler();
    }

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
