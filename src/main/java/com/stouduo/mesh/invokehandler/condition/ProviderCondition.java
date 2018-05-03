package com.stouduo.mesh.invokehandler.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class ProviderCondition implements Condition {
    private final String type = "provider";

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        return type.equalsIgnoreCase(conditionContext.getEnvironment().getProperty("type"));
    }

}