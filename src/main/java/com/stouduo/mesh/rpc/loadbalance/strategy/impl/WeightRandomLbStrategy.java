package com.stouduo.mesh.rpc.loadbalance.strategy.impl;

import java.util.Random;

public class WeightRandomLbStrategy extends WeightLbStrategy {
    private final Random random = new Random();

    @Override
    protected int getIndex(int endpointSize) {
        return random.nextInt(endpointSize);
    }
}
