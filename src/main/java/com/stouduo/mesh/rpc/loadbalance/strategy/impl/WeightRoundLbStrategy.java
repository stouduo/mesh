package com.stouduo.mesh.rpc.loadbalance.strategy.impl;

public class WeightRoundLbStrategy extends WeightLbStrategy {
    private int index = 0;

    @Override
    protected int getIndex(int endpointSize) {
//        index %= endpointSize;
//        logger.info(index + "");
//        return index++;
        return index++ % endpointSize;
    }
}
