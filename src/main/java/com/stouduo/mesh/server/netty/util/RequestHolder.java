package com.stouduo.mesh.server.netty.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestHolder {
    private static Map<Long, Integer> holder = new ConcurrentHashMap<>();

    public static void putRequest(long reqId) {
        holder.put(reqId, 1);
    }

    public static int getRequest(long reqId) {
        return holder.getOrDefault(reqId, 0);
    }

    public static void remove(long reqId) {
        holder.remove(reqId);
    }
}
