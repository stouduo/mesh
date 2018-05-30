package com.stouduo.mesh.dubbo.modify;

import com.stouduo.mesh.dubbo.model.JsonUtils;
import com.stouduo.mesh.dubbo.model.Request;
import com.stouduo.mesh.dubbo.model.RpcInvocation;
import com.stouduo.mesh.dubbo.model.RpcRequestHolder;
import com.stouduo.mesh.rpc.client.ConsumerRpcClient;
import com.stouduo.mesh.rpc.client.RpcRequest;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.FutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.concurrent.CompletableFuture;

public class CustomConsumerRpcClient implements ConsumerRpcClient {
    private Logger logger = LoggerFactory.getLogger(CustomConsumerRpcClient.class);

    private ChannelPoolManager channelPoolManager;

    public CustomConsumerRpcClient(int port, int maxChannels) {
        this.channelPoolManager = new ChannelPoolManager(port, maxChannels);
    }


    public Mono invoke(RpcRequest rpcRequest) throws Exception {
        String interfaceName = rpcRequest.getParameterStr("interface");
        String method = rpcRequest.getParameterStr("method");
        String parameterTypesString = rpcRequest.getParameterStr("parameterTypesString");
        String parameter = rpcRequest.getParameterStr("parameter");

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

        channelPoolManager.acquireFuture().addListener((FutureListener<Channel>) f -> {
            if (f.isSuccess()) {
                Channel channel = f.getNow();
                channel.writeAndFlush(request);
                channelPoolManager.release(channel);
            }
        });
        CompletableFuture<Object> future = new CompletableFuture<>();
        RpcRequestHolder.put(String.valueOf(request.getId()), future);
        return Mono.fromFuture(future);
    }
}
