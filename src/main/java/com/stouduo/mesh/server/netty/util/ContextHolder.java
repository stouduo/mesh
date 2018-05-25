package com.stouduo.mesh.server.netty.util;

import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ContextHolder {
    private static Map<Long, ChannelHandlerContext> holder = new ConcurrentHashMap<>();

    public static void putContext(long reqId, ChannelHandlerContext context) {
        holder.put(reqId, context);
    }

    public static ChannelHandlerContext getContext(long reqId) {
        return holder.getOrDefault(reqId, null);
    }

    public static void remove(long reqId) {
        holder.remove(reqId);
    }
}
