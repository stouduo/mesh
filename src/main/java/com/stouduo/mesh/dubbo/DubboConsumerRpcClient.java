package com.stouduo.mesh.dubbo;

import com.alibaba.fastjson.JSON;
import com.stouduo.mesh.dubbo.model.*;
import com.stouduo.mesh.rpc.client.ConsumerRpcClient;
import com.stouduo.mesh.rpc.client.RpcRequest;
import io.netty.channel.Channel;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

public class DubboConsumerRpcClient implements ConsumerRpcClient {
    private Logger logger = LoggerFactory.getLogger(DubboConsumerRpcClient.class);

    private ConnecManager connectManager;

    public DubboConsumerRpcClient() {
    }

    public DubboConsumerRpcClient setConnectManager(ConnecManager connectManager) {
        this.connectManager = connectManager;
        return this;
    }

    public Object invoke(RpcRequest rpcRequest) throws Exception {
        String interfaceName = rpcRequest.getParameterStr("interface");
        String method = rpcRequest.getParameterStr("method");
        String parameterTypesString = rpcRequest.getParameterStr("parameterTypesString");
        String parameter = rpcRequest.getParameterStr("parameter");
        Channel channel = connectManager.getChannel();

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
        return JSON.parseObject((byte[]) future.get(), Integer.class);
    }
}
