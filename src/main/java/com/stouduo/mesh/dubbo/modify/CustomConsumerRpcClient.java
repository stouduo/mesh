package com.stouduo.mesh.dubbo.modify;

import com.stouduo.mesh.dubbo.model.*;
import com.stouduo.mesh.rpc.client.ConsumerRpcClient;
import com.stouduo.mesh.rpc.client.RpcRequest;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class CustomConsumerRpcClient implements ConsumerRpcClient {
    private Logger logger = LoggerFactory.getLogger(CustomConsumerRpcClient.class);

    private ChannelPoolManager channelPoolManager;

    public CustomConsumerRpcClient(int port, int maxChannels) {
        this.channelPoolManager = new ChannelPoolManager(port, maxChannels);
    }


    public Object invoke(RpcRequest rpcRequest) throws Exception {
        String interfaceName = rpcRequest.getParameterStr("interface");
        String method = rpcRequest.getParameterStr("method");
        String parameterTypesString = rpcRequest.getParameterStr("parameterTypesString");
        String parameter = rpcRequest.getParameterStr("parameter");
        Channel channel = channelPoolManager.acquire();

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

        logger.debug("requestId=" + request.getId());

        RpcFuture future = new RpcFuture();
        RpcRequestHolder.put(String.valueOf(request.getId()), future);

        channel.writeAndFlush(request);
        channelPoolManager.release(channel);
        return Mono.fromFuture(future);
    }
}
