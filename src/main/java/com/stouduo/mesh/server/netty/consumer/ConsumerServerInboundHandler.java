package com.stouduo.mesh.server.netty.consumer;

import com.stouduo.mesh.rpc.RpcRequest;
import com.stouduo.mesh.server.invoke.ConsumerInvokeHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@ChannelHandler.Sharable
public class ConsumerServerInboundHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    @Autowired
    private ConsumerInvokeHandler consumerInvokeHandler;
    private static ExecutorService works = Executors.newFixedThreadPool(4);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest request) {
        try {
            HttpMethod method = request.method();
            RpcRequest rpcRequest = new RpcRequest();
            final Map<String, String> parmMap = new HashMap<>();
            if (method.equals(HttpMethod.GET)) {// 是GET请求
                QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
                decoder.parameters().forEach((k, v) -> {
                    parmMap.put(k, v.get(0));
                });
            } else if (method.equals(HttpMethod.POST)) { // 是POST请求
                HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(request);
                decoder.offer(request);//form
                List<InterfaceHttpData> parmList = decoder.getBodyHttpDatas();
                for (InterfaceHttpData parm : parmList) {
                    Attribute data = (Attribute) parm;
                    parmMap.put(data.getName(), data.getValue());
                }
            } else {
                ;
            }
            if (parmMap.size() != 0) {
                rpcRequest.setParameters(parmMap);
                works.submit(() -> consumerInvokeHandler.invoke(rpcRequest));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
