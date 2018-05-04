package com.stouduo.mesh.rpc.loadbalance.strategy.impl;

import com.stouduo.mesh.rpc.loadbalance.strategy.ILbStrategy;
import com.stouduo.mesh.util.Endpoint;

import java.util.ArrayList;
import java.util.List;

public abstract class WeightLbStrategy implements ILbStrategy {
    private List<Endpoint> weightedEndpoints = new ArrayList<>();

    private List<Endpoint> oldEndpoints;

    @Override
    public synchronized Endpoint lbStrategy(List<Endpoint> endpoints) {
        if (!endpoints.equals(oldEndpoints)) {
            weightedEndpoints.clear();
            endpoints.forEach(endpoint -> {
                int capacity = endpoint.getCapacity();
                while (capacity-- > 0) {
                    weightedEndpoints.add(endpoint);
                }
            });
            oldEndpoints = endpoints;
        }
        return weightedEndpoints.get(getIndex(weightedEndpoints.size()));
    }

    protected abstract int getIndex(int endpointSize);
}
