package com.stouduo.mesh;

import com.stouduo.mesh.registry.IRegistry;
import com.stouduo.mesh.registry.impl.EtcdRegistry;
import com.stouduo.mesh.rpc.loadbalance.strategy.ILbStrategy;
import com.stouduo.mesh.rpc.loadbalance.strategy.impl.*;
import com.stouduo.mesh.server.AgentClient;
import com.stouduo.mesh.server.AgentServer;
import com.stouduo.mesh.server.invoke.ConsumerInvokeHandler;
import com.stouduo.mesh.server.invoke.Invoker;
import com.stouduo.mesh.server.invoke.ProviderInvokeHandler;
import com.stouduo.mesh.server.netty.consumer.ConsumerAgentClient;
import com.stouduo.mesh.server.netty.consumer.ConsumerClientChannelPoolHandler;
import com.stouduo.mesh.server.netty.consumer.ConsumerServerChannelInitializer;
import com.stouduo.mesh.server.netty.provider.ProviderAgentClient;
import com.stouduo.mesh.server.netty.provider.ProviderClientChannelPoolHandler;
import com.stouduo.mesh.server.netty.provider.ProviderServerChannelInitializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.ChannelPoolHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgentConfiguration {
    @Bean("boss")
    public EventLoopGroup bossExecutors() {
        return System.getProperty("os.name").contains("Windows") ? new NioEventLoopGroup(8) : new EpollEventLoopGroup(8);
    }

    @Bean("bizWorker")
    public EventLoopGroup bizExecutors() {
        return new DefaultEventLoopGroup();
    }


    @Bean("ioWorker")
    public EventLoopGroup workerExecutors() {
        return System.getProperty("os.name").contains("Windows") ? new NioEventLoopGroup(8) : new EpollEventLoopGroup(8);
    }

    @Bean
    @ConditionalOnMissingBean(AgentServer.class)
    public ApplicationRunner agentServer() {
        return new AgentServer();
    }


    @Bean
    @ConditionalOnProperty(value = "type", havingValue = "consumer")
    public Invoker consumerInvokeHandler() {
        return new ConsumerInvokeHandler();
    }

    @Bean
    @ConditionalOnProperty(value = "type", havingValue = "provider")
    public Invoker providerInvokeHandler() {
        return new ProviderInvokeHandler();
    }

    @Bean
    @ConditionalOnMissingBean(AgentClient.class)
    @ConditionalOnProperty(value = "type", havingValue = "provider")
    public AgentClient providerAgentClient(@Value("${dubbo.protocol.port:4321}") int port
            , @Value("${agent.client.pool.maxChannels:32}") int maxChannels
            , @Value("${agent.client.ratelimiter:1000}") int rateLimiter) {
        return new ProviderAgentClient(port, maxChannels, rateLimiter);
    }

    @Bean
    @ConditionalOnMissingBean(AgentClient.class)
    @ConditionalOnProperty(value = "type", havingValue = "consumer")
    public AgentClient comsumerAgentClient(@Value("${agent.client.pool.maxChannels:32}") int maxChannels) {
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
