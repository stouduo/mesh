package com.stouduo.mesh;

import com.stouduo.mesh.dubbo.DubboRpcClient;
import com.stouduo.mesh.invokehandler.impl.ConsumerInvokeHandler;
import com.stouduo.mesh.invokehandler.impl.DefaultInvokeHandler;
import com.stouduo.mesh.invokehandler.InvokeHandler;
import com.stouduo.mesh.invokehandler.impl.ProviderInvokeHandler;
import com.stouduo.mesh.registry.IRegistry;
import com.stouduo.mesh.registry.impl.EtcdRegistry;
import com.stouduo.mesh.rpc.client.RpcClient;
import com.stouduo.mesh.rpc.client.impl.AgentRpcClient;
import com.stouduo.mesh.rpc.loadbalance.strategy.ILbStrategy;
import com.stouduo.mesh.rpc.loadbalance.strategy.impl.DefaultLbStrategy;
import com.stouduo.mesh.rpc.loadbalance.strategy.impl.PollLbStrategy;
import com.stouduo.mesh.rpc.loadbalance.strategy.impl.WeightPollLbStrategy;
import com.stouduo.mesh.rpc.loadbalance.strategy.impl.WeightRandomLbStrategy;
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

    @Bean("providerRpc")
    @ConditionalOnMissingBean(name = "providerRpc")
    public RpcClient dubboRpcClient() {
        return new DubboRpcClient();
    }

    @Bean("agentRpc")
    @ConditionalOnMissingBean(name = "agentRpc")
    public RpcClient agentRpcClient() {
        return new AgentRpcClient();
    }

    @Bean
    @ConditionalOnMissingBean(ILbStrategy.class)
    public ILbStrategy defaultLbStrategy() {
        return new DefaultLbStrategy();
    }

    @Bean
    @ConditionalOnProperty(value = "agent.loadbalance.strategy", havingValue = "weightPoll")
    public ILbStrategy weightPollLbStrategy() {
        return new WeightPollLbStrategy();
    }

    @Bean
    @ConditionalOnProperty(value = "agent.loadbalance.strategy", havingValue = "poll")
    public ILbStrategy pollLbStrategy() {
        return new PollLbStrategy();
    }

    @Bean
    @ConditionalOnProperty(value = "agent.loadbalance.strategy", havingValue = "weightRandom")
    public ILbStrategy weightRandomLbStrategy() {
        return new WeightRandomLbStrategy();
    }
}
