package com.stouduo.mesh.dubbo.model;

import java.io.Serializable;

public class RpcResponse implements Serializable {

    private long requestId;
    private Object body;

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "RpcResponse{" +
                "requestId='" + requestId + '\'' +
                ", body=" + body +
                '}';
    }
}
