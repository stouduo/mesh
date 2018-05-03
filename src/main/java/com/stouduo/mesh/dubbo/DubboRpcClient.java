package com.stouduo.mesh.dubbo;

import com.stouduo.mesh.dubbo.model.*;
import com.stouduo.mesh.rpc.client.RpcClient;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class DubboRpcClient implements RpcClient {
    private Logger logger = LoggerFactory.getLogger(DubboRpcClient.class);

    private ConnecManager connectManager;

    public DubboRpcClient() {
        this.connectManager = new ConnecManager();
    }

    public Object invoke(ServerRequest serverRequest) throws Exception {
        String interfaceName = serverRequest.queryParam("interface").get();
        String method = serverRequest.queryParam("method").get();
        String parameterTypesString = serverRequest.queryParam("parameterTypesString").get();
        String parameter = serverRequest.queryParam("parameter").get();
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

        logger.info("requestId=" + request.getId());

        RpcFuture future = new RpcFuture();
        RpcRequestHolder.put(String.valueOf(request.getId()), future);

        channel.writeAndFlush(request);

        Object result = null;
        try {
            result = future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
