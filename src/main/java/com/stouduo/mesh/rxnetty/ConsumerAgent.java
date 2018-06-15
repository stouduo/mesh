package com.stouduo.mesh.rxnetty;

import com.alibaba.fastjson.JSON;
import com.stouduo.mesh.util.IpHelper;
import io.netty.buffer.ByteBuf;
import io.netty.handler.logging.LogLevel;
import io.reactivex.netty.client.ConnectionRequest;
import io.reactivex.netty.protocol.http.server.HttpServer;
import io.reactivex.netty.protocol.tcp.client.TcpClient;

public class ConsumerAgent {
    public static void main(String[] args) {
        final TcpClient<ByteBuf, ByteBuf> client = TcpClient.newClient(IpHelper.getHostIp(), 20001);
        HttpServer<ByteBuf, ByteBuf> server;
        ConnectionRequest<ByteBuf, ByteBuf> connReq = client.createConnectionRequest();
        server = HttpServer.newServer(20000)
                .enableWireLogging("consumer-agent-server", LogLevel.INFO)
                .start((serverReq, serverResp) -> serverResp.writeStringAndFlushOnEach(connReq.flatMap(request -> {
                    return request.writeAndFlushOnEach(serverReq.getContent()).cast(String.class).mergeWith(request.getInput().map(response -> {
                        byte[] resp = new byte[response.readableBytes() - 2];
                        response.skipBytes(2);
                        response.readBytes(resp);
                        return JSON.parseObject(resp, String.class).toString();
                    }));
                })));
        server.awaitShutdown();
    }
}
