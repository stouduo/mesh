package com.stouduo.mesh.server.netty.provider;

import com.stouduo.mesh.dubbo.model.JsonUtils;
import com.stouduo.mesh.dubbo.model.Request;
import com.stouduo.mesh.dubbo.model.RpcDTO;
import com.stouduo.mesh.dubbo.model.RpcInvocation;
import com.stouduo.mesh.server.AgentClient;
import com.stouduo.mesh.util.IpHelper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.nio.NioEventLoopGroup;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.UrlEncoded;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.util.Map;

public class ProviderAgentClient extends AgentClient {
    private InetSocketAddress inetSocketAddress;

    public ProviderAgentClient(int serverPort, int maxChannels) {
        this.workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
        this.maxChannels = maxChannels;
        this.inetSocketAddress = new InetSocketAddress(IpHelper.getHostIp(), serverPort);
    }

    @Override
    public void invoke(RpcDTO data) {
        asycSend(inetSocketAddress, parseData(data));
    }


    private Request parseData(RpcDTO data) {
        try {
            ByteBuf paramContent = data.getContent();
            byte[] paramBytes = new byte[paramContent.readableBytes()];
            paramContent.readBytes(paramBytes);
            paramContent.release();
            Map<String, String[]> params = paramParams(new String(paramBytes, "UTF-8"));
            RpcInvocation invocation = new RpcInvocation();
            invocation.setMethodName(params.get("method")[0]);
            invocation.setAttachment("path", params.get("interface")[0]);
            invocation.setParameterTypes(params.get("parameterTypesString")[0]);    // Dubbo内部用"Ljava/lang/String"来表示参数类型是String
            Request request = new Request();
            request.setId(data.getSessionId());
            request.setVersion("2.0.0");
            request.setTwoWay(true);
            request.setData(encodeRpcInvocation(invocation, params.get("parameter")[0]));
            return request;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Map<String, String[]> paramParams(String paramStr) {
        MultiMap<String> ret = new MultiMap<>();
        UrlEncoded.decodeUtf8To(paramStr, ret);
        return ret.toStringArrayMap();
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
