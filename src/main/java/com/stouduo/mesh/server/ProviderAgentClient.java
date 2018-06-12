package com.stouduo.mesh.server;

import com.stouduo.mesh.dubbo.model.JsonUtils;
import com.stouduo.mesh.dubbo.model.Request;
import com.stouduo.mesh.dubbo.model.RpcInvocation;
import com.stouduo.mesh.util.FutureHolder;
import com.stouduo.mesh.util.IpHelper;
import io.netty.channel.nio.NioEventLoopGroup;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ProviderAgentClient extends AgentClient {
    private InetSocketAddress inetSocketAddress;

    public ProviderAgentClient(int serverPort, int maxChannels) {
        this.workerGroup = new NioEventLoopGroup();
        this.maxChannels = maxChannels;
        this.inetSocketAddress = new InetSocketAddress(IpHelper.getHostIp(), serverPort);
    }

    @Override
    public CompletableFuture<String> invoke(Object data) {
        Request request = parseData((HttpServletRequest) data);
        CompletableFuture<String> responseFuture = new CompletableFuture<>();
        FutureHolder.put(request.getId(), responseFuture);
        asycSend(inetSocketAddress, request);
        return responseFuture;
    }


    private Request parseData(HttpServletRequest data) {
        try {
            RpcInvocation invocation = new RpcInvocation();
            invocation.setMethodName(data.getParameter("method"));
            invocation.setAttachment("path", data.getParameter("interface"));
            invocation.setParameterTypes(data.getParameter("parameterTypesString"));    // Dubbo内部用"Ljava/lang/String"来表示参数类型是String
            Request request = new Request();
            request.setVersion("2.0.0");
            request.setTwoWay(true);
            request.setData(encodeRpcInvocation(invocation, data.getParameter("parameter")));
            return request;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] encodeRpcInvocation(RpcInvocation inv, String arguments) throws Exception {
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
