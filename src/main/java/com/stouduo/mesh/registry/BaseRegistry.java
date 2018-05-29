package com.stouduo.mesh.registry;

import com.stouduo.mesh.rpc.loadbalance.strategy.ILbStrategy;
import com.stouduo.mesh.rpc.loadbalance.strategy.impl.WeightLbStrategy;
import com.stouduo.mesh.util.Endpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BaseRegistry implements AutoCloseable {
    @Value("${agent.registry.rootPath:stouduo}")
    protected String rootPath;

    @Value("${etcd.url:http://localhost:2379}")
    protected String serverUrl;

    @Value("${type:default}")
    protected String serverType;

    @Value("${server.port:30000}")
    protected String serverPort;

    @Value("${agent.provider.serverName:com.alibaba.dubbo.performance.demo.provider.IHelloService}")
    protected String serverName;

    @Value("${agent.provider.serverCapacity:1}")
    protected String serverCapacity;

    @Autowired
    private ILbStrategy iLbStrategy;

    protected static Map<String, List<Endpoint>> providers = new ConcurrentHashMap<>();
    protected static Map<String, List<Endpoint>> weightedProviders = new ConcurrentHashMap<>();

    public String getServerCapacity() {
        return serverCapacity;
    }

    public void setServerCapacity(String serverCapacity) {
        this.serverCapacity = serverCapacity;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public boolean isProvider() {
        return "provider".equalsIgnoreCase(this.serverType);
    }

    protected boolean isWeightLbStrategy() {
        return iLbStrategy instanceof WeightLbStrategy;
    }

    @Override
    public void close() throws Exception {
        this.providers.clear();
        this.providers = null;
        this.weightedProviders.clear();
        this.weightedProviders = null;
    }
}
