package com.stouduo.mesh.rpc.client;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class RpcRequest {
    private static AtomicLong atomicLong = new AtomicLong();
    private long id;
    private Map<String, Object> parameters;
    private String requsetUrl;

    public RpcRequest(String requsetUrl) {
        this.id = atomicLong.getAndIncrement();
        this.parameters = new HashMap<>();
        this.requsetUrl = requsetUrl;
    }

    public RpcRequest() {
        this("");
    }

    public RpcRequest setAttribute(String name, String value) {
        this.parameters.put(name, value);
        return this;
    }

    public RpcRequest setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
        return this;
    }


    public long getId() {
        return id;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public String getParameterStr(String paramName) {
        return (String) this.parameters.get(paramName);
    }

    public Object getParammeter(String name) {
        return this.parameters.get(name);
    }

    public String getRequsetUrl() {
        return this.requsetUrl;
    }
}
