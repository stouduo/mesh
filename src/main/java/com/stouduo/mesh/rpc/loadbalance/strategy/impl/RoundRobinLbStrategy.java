package com.stouduo.mesh.rpc.loadbalance.strategy.impl;

import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLbStrategy extends AbstractLbStrategy {
    private static AtomicInteger index = new AtomicInteger(0);

    protected int getIndex(int endpointSize) {
        return index.getAndIncrement() % endpointSize;
    }
}
