package com.stouduo.mesh.rpc.loadbalance.strategy.impl;

import java.util.concurrent.atomic.AtomicInteger;

public class WeightPollLbStrategy extends WeightLbStrategy {
    private static AtomicInteger index = new AtomicInteger(0);

    @Override
    protected int getIndex(int endpointSize) {
        if (index.get() >= endpointSize) {
            index.compareAndSet(index.get(), 0);
        }
        return index.getAndIncrement() % endpointSize;
    }
}
