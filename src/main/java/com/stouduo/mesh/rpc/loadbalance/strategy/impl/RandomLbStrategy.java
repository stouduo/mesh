package com.stouduo.mesh.rpc.loadbalance.strategy.impl;

import java.util.Random;

public class RandomLbStrategy extends AbstractLbStrategy {
    private final Random random = new Random();

    @Override
    protected int getIndex(int endpointSize) {
        return random.nextInt(endpointSize);
    }
}
