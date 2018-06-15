package com.stouduo.mesh.rxnetty.http;

import com.stouduo.mesh.util.IpHelper;
import io.netty.buffer.ByteBuf;
import io.netty.handler.logging.LogLevel;
import io.reactivex.netty.protocol.http.client.HttpClient;
import io.reactivex.netty.protocol.http.client.HttpClientRequest;
import io.reactivex.netty.protocol.http.server.HttpServer;

import java.util.Iterator;
import java.util.Map.*;

public class ConsumerAgent {
    public static void main(String[] args) {
        final HttpClient<ByteBuf, ByteBuf> targetClient = HttpClient.newClient(IpHelper.getHostIp(), 20001);
        HttpServer.newServer(20000)
                .enableWireLogging("proxy-server", LogLevel.DEBUG)
                .start((serverReq, serverResp) -> {
                            final HttpClientRequest<ByteBuf, ByteBuf> clientReq =
                                    targetClient.createRequest(serverReq.getHttpMethod(), serverReq.getUri());
                            Iterator<Entry<CharSequence, CharSequence>> serverReqHeaders = serverReq.headerIterator();
                            serverReqHeaders.forEachRemaining(header -> clientReq.setHeader(header.getKey(), header.getValue()));
                            return clientReq.writeContent(serverReq.getContent())
                                    .flatMap(clientResp -> {
                                        clientResp.headerIterator().forEachRemaining(header -> serverResp.setHeader(header.getKey(), header.getValue()));
                                        serverResp.setHeader("X-Proxied-By", "RxNetty");
                                        return serverResp.write(clientResp.getContent());
                                    });
                        }
                ).awaitShutdown();
    }
}
