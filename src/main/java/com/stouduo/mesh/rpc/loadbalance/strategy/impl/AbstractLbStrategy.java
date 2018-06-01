package com.stouduo.mesh.rpc.loadbalance.strategy.impl;

import com.stouduo.mesh.dubbo.model.RpcDTO.Endpoint;
import com.stouduo.mesh.rpc.loadbalance.strategy.ILbStrategy;

import java.util.List;

public abstract class AbstractLbStrategy implements ILbStrategy {

    @Override
    public synchronized Endpoint lbStrategy(List<Endpoint> endpoints) {
        return endpoints.get(getIndex(endpoints.size()));
    }


    protected abstract int getIndex(int endpointSize);
}
