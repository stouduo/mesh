package com.stouduo.mesh.registry;

import com.stouduo.mesh.util.Endpoint;
import com.stouduo.mesh.util.IpHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BaseRegistry implements AutoCloseable {
    @Value("${agent.registry.rootPath:stouduo}")
    protected String rootPath;

    @Value("${agent.registry.serverUrl:http://localhost:2379}")
    protected String serverUrl;

    @Value("${type:default}")
    protected String serverType;

    @Value("${server.port:30000}")
    protected int serverPort;

    @Value("${agent.provider.serverName:com.stouduo.agentmesh}")
    protected String serverName;

    @Value("${agent.provider.serverCapacity:1}")
    protected int serverCapacity;

    private Endpoint currentEndpoint;

    protected static Map<String, List<Endpoint>> providers = new ConcurrentHashMap<>();

    public int getServerCapacity() {
        return serverCapacity;
    }

    public void setServerCapacity(int serverCapacity) {
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

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public boolean isProvider() {
        return "provider".equalsIgnoreCase(this.serverType);
    }

    public Endpoint currentEndpoint() {
        if (currentEndpoint == null) {
            try {
                currentEndpoint = new Endpoint(IpHelper.getHostIp(), serverPort, serverCapacity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return currentEndpoint;
    }

    @Override
    public void close() throws Exception {
        this.providers.clear();
        this.providers = null;
    }

}
