package com.stouduo.mesh.rpc.loadbalance.strategy.impl;

import com.stouduo.mesh.rpc.loadbalance.strategy.ILbStrategy;
import com.stouduo.mesh.util.Endpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class WeightLbStrategy implements ILbStrategy {
    protected Logger logger = LoggerFactory.getLogger(WeightLbStrategy.class);
    private List<Endpoint> weightedEndpoints = new ArrayList<>();

    private List<Endpoint> oldEndpoints;
    private int serverSize = 0;

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
            serverSize = weightedEndpoints.size();
            oldEndpoints = endpoints;
            logger.info(">>>>>加权服务列表：" + weightedEndpoints.toString());
        }
        return weightedEndpoints.get(getIndex(serverSize));
    }

    protected abstract int getIndex(int endpointSize);
}
