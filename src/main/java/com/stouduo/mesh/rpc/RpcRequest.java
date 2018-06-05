package com.stouduo.mesh.rpc;


import com.stouduo.mesh.util.Endpoint;
import io.netty.buffer.ByteBuf;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class RpcRequest implements Serializable {
    private ByteBuf content;
    private Endpoint remoteServer;
    private int id;
    private static AtomicInteger idGenerator = new AtomicInteger(0);

    public RpcRequest(Endpoint remoteServer) {
        this.remoteServer = remoteServer;
        this.id = idGenerator.getAndIncrement();
    }

    public int getId() {
        return this.id;
    }

    public RpcRequest() {
        this(null);
    }

    public ByteBuf getContent() {
        return content;
    }

    public RpcRequest setContent(ByteBuf content) {
        this.content = content;
        return this;
    }

    public Endpoint getRemoteServer() {
        return remoteServer;
    }

    public RpcRequest setRemoteServer(Endpoint remoteServer) {
        this.remoteServer = remoteServer;
        return this;
    }
}
