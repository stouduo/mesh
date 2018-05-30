package com.stouduo.mesh.dubbo.model;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class RpcRequestHolder {

    // key: requestId     value: RpcFuture
    private static ConcurrentHashMap<String, CompletableFuture<Object>> processingRpc = new ConcurrentHashMap<>();

    public static void put(String requestId, CompletableFuture rpcFuture) {
        processingRpc.put(requestId, rpcFuture);
    }

    public static CompletableFuture get(String requestId) {
        return processingRpc.get(requestId);
    }

    public static void remove(String requestId) {
        processingRpc.remove(requestId);
    }
}
