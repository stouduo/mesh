package com.stouduo.mesh.server.netty.provider;

import com.stouduo.mesh.dubbo.model.*;
import com.stouduo.mesh.dubbo.model.RpcDTO.RpcRequest;
import com.stouduo.mesh.server.AgentClient;
import com.stouduo.mesh.util.IpHelper;
import io.netty.channel.nio.NioEventLoopGroup;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;

public class ProviderAgentClient extends AgentClient {
    private InetSocketAddress inetSocketAddress;

    public ProviderAgentClient(int serverPort, int maxChannels) {
        this.workerGroup = new NioEventLoopGroup(4);
        this.maxChannels = maxChannels;
        this.inetSocketAddress = new InetSocketAddress(IpHelper.getHostIp(), serverPort);
    }

    @Override
    public void invoke(RpcRequest rpcRequest) {
        try {
            String interfaceName = rpcRequest.getParametersOrThrow("interface");
            String method = rpcRequest.getParametersOrThrow("method");
            String parameterTypesString = rpcRequest.getParametersOrThrow("parameterTypesString");
            String parameter = rpcRequest.getParametersOrThrow("parameter");

            RpcInvocation invocation = new RpcInvocation();
            invocation.setMethodName(method);
            invocation.setAttachment("path", interfaceName);
            invocation.setParameterTypes(parameterTypesString);    // Dubbo内部用"Ljava/lang/String"来表示参数类型是String
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(out));
            JsonUtils.writeObject(parameter, writer);
            invocation.setArguments(out.toByteArray());
            Request request = new Request();
            request.setId(rpcRequest.getId());
            request.setVersion("2.0.0");
            request.setTwoWay(true);
            request.setData(invocation);
            asycSend(inetSocketAddress, request);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
