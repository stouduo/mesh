package com.stouduo.mesh.rpc.loadbalance.strategy.impl;

import com.stouduo.mesh.rpc.loadbalance.strategy.ILbStrategy;
import com.stouduo.mesh.util.Endpoint;

import java.util.List;

public abstract class AbstractLbStrategy implements ILbStrategy {

    @Override
    public synchronized Endpoint lbStrategy(List<Endpoint> endpoints) {
        return endpoints.get(getIndex(endpoints.size()));
    }


    protected abstract int getIndex(int endpointSize);
}
