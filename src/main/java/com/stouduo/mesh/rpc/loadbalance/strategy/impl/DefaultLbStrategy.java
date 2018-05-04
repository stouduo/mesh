package com.stouduo.mesh.rpc.loadbalance.strategy.impl;

import com.stouduo.mesh.rpc.loadbalance.strategy.ILbStrategy;
import com.stouduo.mesh.util.Endpoint;

import java.util.List;
import java.util.Random;

public class DefaultLbStrategy implements ILbStrategy {
    private final Random random = new Random();

    @Override
    public Endpoint lbStrategy(List<Endpoint> endpoints) {
        return endpoints.get(random.nextInt(endpoints.size()));
    }
}
