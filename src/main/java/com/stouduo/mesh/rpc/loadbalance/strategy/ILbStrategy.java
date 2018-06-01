package com.stouduo.mesh.rpc.loadbalance.strategy;


import com.stouduo.mesh.dubbo.model.RpcDTO.Endpoint;

import java.util.List;

public interface ILbStrategy {
    Endpoint lbStrategy(List<Endpoint> endpoints);
}
