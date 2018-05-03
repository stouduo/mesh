package com.stouduo.mesh;

import com.stouduo.mesh.dubbo.DubboRpcClient;
import com.stouduo.mesh.invokehandler.ConsumerInvokeHandler;
import com.stouduo.mesh.invokehandler.DefaultInvokeHandler;
import com.stouduo.mesh.invokehandler.InvokeHandler;
import com.stouduo.mesh.invokehandler.ProviderInvokeHandler;
import com.stouduo.mesh.invokehandler.condition.ConsumerCondition;
import com.stouduo.mesh.invokehandler.condition.ProviderCondition;
import com.stouduo.mesh.registry.IRegistry;
import com.stouduo.mesh.registry.etcd.EtcdRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgentConfiguration {

    @Bean
    @Conditional(ConsumerCondition.class)
    public InvokeHandler consumerInvokerHandler() {
        return new ConsumerInvokeHandler();
    }

    @Bean
    @Conditional(ProviderCondition.class)
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
    public EtcdRegistry etcdRegistry() {
        return new EtcdRegistry();
    }

    @Bean("dubboRpc")
    @ConditionalOnProperty(value = "agent.target.rpc", havingValue = "dubbo")
    public DubboRpcClient dubboRpcClient() {
        return new DubboRpcClient();
    }
}
