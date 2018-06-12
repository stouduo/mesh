package com.stouduo.mesh.util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;


public class FutureHolder {
    private static ConcurrentHashMap<Long, CompletableFuture<String>> futures = new ConcurrentHashMap<>();

    public static void put(long sessionId, CompletableFuture<String> future) {
        futures.put(sessionId, future);
    }

    public static CompletableFuture<String> get(long sessionId) {
        return futures.getOrDefault(sessionId, null);
    }

    public static void remove(long sessionId) {
        futures.remove(sessionId);
    }
}
