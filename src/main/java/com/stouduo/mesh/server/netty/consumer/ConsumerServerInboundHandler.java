package com.stouduo.mesh.server.netty.consumer;

import com.stouduo.mesh.dubbo.model.RpcDTO.RpcRequest;
import com.stouduo.mesh.server.invoke.ConsumerInvokeHandler;
import com.stouduo.mesh.server.netty.util.ContextHolder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@ChannelHandler.Sharable
public class ConsumerServerInboundHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static AtomicInteger reqId = new AtomicInteger(0);
    @Autowired
    private ConsumerInvokeHandler consumerInvokeHandler;
//    private static ExecutorService works = Executors.newFixedThreadPool(8);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest request) {
        try {
            HttpMethod method = request.method();
            final Map<String, String> parmMap = new HashMap<>();
            if (method.equals(HttpMethod.GET)) {// 是GET请求
                QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
                decoder.parameters().forEach((k, v) -> parmMap.put(k, v.get(0)));
            } else if (method.equals(HttpMethod.POST)) { // 是POST请求
                HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(request);
                decoder.getBodyHttpDatas().stream().map(param -> (Attribute) param).forEach(param -> {
                    try {
                        parmMap.put(param.getName(), param.getValue());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                decoder.destroy();
            } else {
                ;
            }
            if (parmMap.size() != 0) {
                RpcRequest.Builder builder = RpcRequest.newBuilder();
                builder.putAllParameters(parmMap).setId(reqId.getAndIncrement());
                ContextHolder.putContext(builder.getId(), channelHandlerContext);
                consumerInvokeHandler.invoke(builder.build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
