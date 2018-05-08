package com.stouduo.mesh.rpc.client;


import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class RpcRequest {
    private static AtomicLong atomicLong = new AtomicLong();
    private long id;
    private Map<String, String> parameters;
    private String remoteUrl;
    private MultiValueMap<String, String> multiParameters;

    public RpcRequest(String remoteUrl) {
        this.id = atomicLong.getAndIncrement();
        this.parameters = new HashMap<>();
        this.remoteUrl = remoteUrl;
        this.multiParameters = new LinkedMultiValueMap<>();
    }

    public RpcRequest() {
        this("");
    }

    public RpcRequest setAttribute(String name, String value) {
        this.parameters.put(name, value);
        return this;
    }

    public MultiValueMap<String, String> getMultiParameters() {
        return multiParameters;
    }

    public RpcRequest setMultiParameters(MultiValueMap<String, String> multiParameters) {
        this.multiParameters = multiParameters;
        this.parameters = multiParameters.toSingleValueMap();
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

    public String getRemoteUrl() {
        return this.remoteUrl;
    }

}
