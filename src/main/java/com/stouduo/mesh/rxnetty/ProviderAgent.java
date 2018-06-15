package com.stouduo.mesh.rxnetty;

import com.stouduo.mesh.dubbo.DubboRpcDecoder;
import com.stouduo.mesh.dubbo.DubboRpcEncoder;
import com.stouduo.mesh.dubbo.model.JsonUtils;
import com.stouduo.mesh.dubbo.model.Request;
import com.stouduo.mesh.dubbo.model.RpcInvocation;
import com.stouduo.mesh.util.IpHelper;
import io.netty.buffer.ByteBuf;
import io.netty.handler.logging.LogLevel;
import io.reactivex.netty.client.ConnectionRequest;
import io.reactivex.netty.protocol.tcp.client.TcpClient;
import io.reactivex.netty.protocol.tcp.server.TcpServer;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.UrlEncoded;
import rx.Observable;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.SQLOutput;
import java.util.Map;

public class ProviderAgent {
    public static void main(String[] args) {
        final TcpClient<Request, ByteBuf> targetClient = TcpClient.newClient(IpHelper.getHostIp(), 20890).pipelineConfigurator(pipeline -> {
            pipeline.addLast(new DubboRpcEncoder()).addLast(new DubboRpcDecoder());
        });
        /*Create a new connection request, each subscription to which creates a new connection.*/
        ConnectionRequest<Request, ByteBuf> connReq = targetClient.createConnectionRequest();

        TcpServer<ByteBuf, ByteBuf> server;

        /*Starts a new HTTP server on an ephemeral port which acts as a proxy to the target server started above.*/
        server = TcpServer.newServer(20001)
                .enableWireLogging("provider-agent-server", LogLevel.INFO)
                .start(serverConn -> serverConn.writeAndFlushOnEach(connReq.flatMap(clientConn -> {
                    return clientConn.writeAndFlushOnEach(serverConn.getInput().map(ProviderAgent::parseData))
                            .cast(ByteBuf.class).mergeWith(clientConn.getInput());
                })));
        server.awaitShutdown();
    }

    private static Request parseData(ByteBuf paramContent) {
        try {
            byte[] paramBytes = new byte[paramContent.readableBytes()];
            paramContent.readBytes(paramBytes);
            paramContent.release();
            Map<String, String[]> params = paramParams(new String(paramBytes, "UTF-8"));
            RpcInvocation invocation = new RpcInvocation();
            invocation.setMethodName(params.get("method")[0]);
            invocation.setAttachment("path", params.get("interface")[0]);
            invocation.setParameterTypes(params.get("parameterTypesString")[0]);    // Dubbo内部用"Ljava/lang/String"来表示参数类型是String
            Request request = new Request();
            request.setVersion("2.0.0");
            request.setTwoWay(true);
            request.setData(encodeRpcInvocation(invocation, params.get("parameter")[0]));
            return request;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Map<String, String[]> paramParams(String paramStr) {
        MultiMap<String> ret = new MultiMap<>();
        UrlEncoded.decodeUtf8To(paramStr, ret);
        return ret.toStringArrayMap();
    }

    public static byte[] encodeRpcInvocation(RpcInvocation inv, String arguments) throws Exception {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(bos))) {
            JsonUtils.writeObject(arguments, writer);
            inv.setArguments(bos.toByteArray());
            bos.reset();
            JsonUtils.writeObject(inv.getAttachment("dubbo", "2.0.1"), writer);
            JsonUtils.writeObject(inv.getAttachment("path"), writer);
            JsonUtils.writeObject(inv.getAttachment("version"), writer);
            JsonUtils.writeObject(inv.getMethodName(), writer);
            JsonUtils.writeObject(inv.getParameterTypes(), writer);

            JsonUtils.writeBytes(inv.getArguments(), writer);
            JsonUtils.writeObject(inv.getAttachments(), writer);
            return bos.toByteArray();
        }

    }
}
