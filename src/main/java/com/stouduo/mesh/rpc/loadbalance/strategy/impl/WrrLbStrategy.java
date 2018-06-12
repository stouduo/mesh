package com.stouduo.mesh.rpc.loadbalance.strategy.impl;

import com.stouduo.mesh.rpc.loadbalance.strategy.ILbStrategy;
import com.stouduo.mesh.util.Endpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 平滑加权轮询
 */
public class WrrLbStrategy implements ILbStrategy {
    private static Logger logger = LoggerFactory.getLogger(WeightLbStrategy.class);
    private List<Endpoint> oldEndpoints;
    private List<Endpoint> cachedEndpoints = new ArrayList<>();
    private int index = 0;
    private int totalCapacity;

    @Override
    public synchronized Endpoint lbStrategy(List<Endpoint> endpoints) {
        if (endpoints != null && !endpoints.equals(oldEndpoints)) {
            oldEndpoints = endpoints;
            cachedEndpoints.clear();
            totalCapacity = endpoints.size();
        }
//        logger.info(cachedEndpoints.toString() + "/当前index为：" + index);
        return cachedEndpoints.size() < totalCapacity ? getNextEndpoint(oldEndpoints) : cachedEndpoints.get(index++ % cachedEndpoints.size());
//        return getNextEndpoint(endpoints);
    }

    private Endpoint getNextEndpoint(List<Endpoint> endpoints) {
        Endpoint ret = null;
        int total = 0;

        for (int i = 0; i < endpoints.size(); i++) {
            Endpoint temp = endpoints.get(i);
            temp.setCurrentCapacity(temp.getCapacity() + temp.getCurrentCapacity());
            total += temp.getCapacity();

            if (ret == null || ret.getCurrentCapacity() < temp.getCurrentCapacity()) {
                ret = temp;
            }
        }
        totalCapacity = total;
        ret.setCurrentCapacity(ret.getCurrentCapacity() - total);
        cachedEndpoints.add(ret);
        return ret;
    }
}
