package com.stouduo.mesh.dubbo.model;

public class RpcResponse {

    private String requestId;
    private Object body;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Object getBytes() {
        return body;
    }

    public void setBytes(Object body) {
        this.body = body;
    }
}
