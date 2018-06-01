package com.stouduo.mesh.dubbo.model;


import com.stouduo.mesh.util.Endpoint;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class RpcRequest implements Serializable {
    private static AtomicLong atomicLong = new AtomicLong();
    private long id;
    private Map<String, String> parameters;
    private Endpoint remoteServer;

    public RpcRequest(Endpoint remoteServer) {
        this.id = atomicLong.getAndIncrement();
        this.parameters = new HashMap<>();
        this.remoteServer = remoteServer;
    }

    public RpcRequest() {
        this(null);
    }

    public RpcRequest setAttribute(String name, String value) {
        this.parameters.put(name, value);
        return this;
    }


    public RpcRequest setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
        return this;
    }

    public long getId() {
        return id;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public String getParameterStr(String paramName) {
        return (String) this.parameters.get(paramName);
    }

    public Object getParammeter(String name) {
        return this.parameters.get(name);
    }

    public Endpoint getRemoteServer() {
        return remoteServer;
    }

    public void setRemoteServer(Endpoint remoteServer) {
        this.remoteServer = remoteServer;
    }
}
