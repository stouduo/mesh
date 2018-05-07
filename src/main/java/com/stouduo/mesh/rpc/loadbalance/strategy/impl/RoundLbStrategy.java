package com.stouduo.mesh.rpc.loadbalance.strategy.impl;

import com.stouduo.mesh.rpc.loadbalance.strategy.ILbStrategy;
import com.stouduo.mesh.util.Endpoint;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundLbStrategy implements ILbStrategy {
    @Override
    public Endpoint lbStrategy(List<Endpoint> endpoints) {
        return endpoints.get(getIndex(endpoints.size()));
    }

    private static AtomicInteger index = new AtomicInteger(0);

    protected int getIndex(int endpointSize) {
        if (index.get() >= endpointSize) {
            index.compareAndSet(index.get(), 0);
        }
        return index.getAndIncrement() % endpointSize;
    }
}
