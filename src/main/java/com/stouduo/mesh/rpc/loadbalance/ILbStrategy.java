package com.stouduo.mesh.rpc.loadbalance;

import com.stouduo.mesh.util.Endpoint;

import java.util.List;

public interface ILbStrategy {
    Endpoint lbStrategy(List<Endpoint> endpoints);
}
