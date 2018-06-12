package com.stouduo.mesh.dubbo.model;

import com.stouduo.mesh.util.Endpoint;
import io.netty.buffer.ByteBuf;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.ReferenceCounted;

import java.util.concurrent.atomic.AtomicLong;

public class RpcDTO {
    private static AtomicLong idGenerator = new AtomicLong(0);
    private long sessionId;
    private Object content;
    private Endpoint remoteServer;

    public RpcDTO() {
        this.sessionId = idGenerator.getAndIncrement();
    }


    public long getSessionId() {
        return sessionId;
    }

    public RpcDTO setSessionId(long sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public Object getContent() {
        return content;
    }

    public RpcDTO setContent(Object content) {
        this.content = content;
        return this;
    }

    public Endpoint getRemoteServer() {
        return remoteServer;
    }

    public RpcDTO setRemoteServer(Endpoint remoteServer) {
        this.remoteServer = remoteServer;
        return this;
    }

}
