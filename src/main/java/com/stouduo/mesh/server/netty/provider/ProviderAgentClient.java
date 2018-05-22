package com.stouduo.mesh.server.netty.provider;

import com.stouduo.mesh.dubbo.model.*;
import com.stouduo.mesh.rpc.RpcRequest;
import com.stouduo.mesh.server.AgentClient;
import com.stouduo.mesh.util.IpHelper;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.SimpleChannelPool;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;

public class ProviderAgentClient extends AgentClient {

    public ProviderAgentClient(int serverPort, int maxChannels) {
        this.workerGroup = new NioEventLoopGroup();
        this.maxChannels = maxChannels;
        this.inetSocketAddress = new InetSocketAddress(IpHelper.getHostIp(), serverPort);
    }

    @Override
    public Object invoke(RpcRequest rpcRequest) {
        try {
            String interfaceName = rpcRequest.getParameterStr("interface");
            String method = rpcRequest.getParameterStr("method");
            String parameterTypesString = rpcRequest.getParameterStr("parameterTypesString");
            String parameter = rpcRequest.getParameterStr("parameter");
            SimpleChannelPool pool = poolMap.get(inetSocketAddress);
            Channel channel = pool.acquire().get();
            RpcInvocation invocation = new RpcInvocation();
            invocation.setMethodName(method);
            invocation.setAttachment("path", interfaceName);
            invocation.setParameterTypes(parameterTypesString);    // Dubbo内部用"Ljava/lang/String"来表示参数类型是String
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(out));
            JsonUtils.writeObject(parameter, writer);
            invocation.setArguments(out.toByteArray());
            Request request = new Request();
            request.setVersion("2.0.0");
            request.setTwoWay(true);
            request.setData(invocation);
            RpcFuture future = new RpcFuture();
            RpcRequestHolder.put(String.valueOf(request.getId()), future);
            channel.writeAndFlush(request);
            pool.release(channel);
            return future.get();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }
}
