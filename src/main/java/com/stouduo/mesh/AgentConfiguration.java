package com.stouduo.mesh;

import com.stouduo.mesh.invokehandler.ConsumerInvokeHandler;
import com.stouduo.mesh.invokehandler.DefaultInvokeHandler;
import com.stouduo.mesh.invokehandler.InvokeHandler;
import com.stouduo.mesh.invokehandler.ProviderInvokeHandler;
import com.stouduo.mesh.invokehandler.condition.ConsumerCondition;
import com.stouduo.mesh.invokehandler.condition.ProviderCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
}
