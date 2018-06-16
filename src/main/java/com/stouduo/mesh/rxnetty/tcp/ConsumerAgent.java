package com.stouduo.mesh.rxnetty.tcp;

import com.alibaba.fastjson.JSON;
import com.stouduo.mesh.util.IpHelper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.logging.LogLevel;
import io.reactivex.netty.client.ConnectionRequest;
import io.reactivex.netty.protocol.http.server.HttpServer;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import io.reactivex.netty.protocol.http.server.HttpServerResponseImpl;
import io.reactivex.netty.protocol.tcp.client.TcpClient;

public class ConsumerAgent {
    public static void main(String[] args) {
        final TcpClient<ByteBuf, ByteBuf> client = TcpClient.newClient(IpHelper.getHostIp(), 20001);
        ConnectionRequest<ByteBuf, ByteBuf> connReq = client.createConnectionRequest();
        HttpServer.newServer(20000)
                .enableWireLogging("consumer-agent-server", LogLevel.INFO)
                .start((serverReq, serverResp) -> serverResp
                        .setHeader(HttpHeaderNames.CONTENT_TYPE, "application/json")
                        .setStatus(HttpResponseStatus.OK)
                        .writeAndFlushOnEach(connReq.flatMap(channel ->channel
                                .writeAndFlushOnEach(serverReq.getContent())
                                .cast(ByteBuf.class)
                                .mergeWith(channel.getInput()))))
                .awaitShutdown();
    }
}
