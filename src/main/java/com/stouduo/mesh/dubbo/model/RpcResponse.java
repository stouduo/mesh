package com.stouduo.mesh.dubbo.model;

import java.util.Arrays;

public class RpcResponse {

    private String requestId;
    private byte[] bytes;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public String toString() {
        return "RpcResponse{" +
                "requestId='" + requestId + '\'' +
                ", bytes=" + Arrays.toString(bytes) +
                '}';
    }
}
