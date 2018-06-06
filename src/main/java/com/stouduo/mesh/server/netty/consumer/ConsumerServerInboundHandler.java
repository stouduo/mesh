package com.stouduo.mesh.server.netty.consumer;

import com.stouduo.mesh.dubbo.model.RpcDTO;
import com.stouduo.mesh.server.invoke.ConsumerInvokeHandler;
import com.stouduo.mesh.server.netty.util.ContextHolder;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
@ChannelHandler.Sharable
public class ConsumerServerInboundHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    @Autowired
    private ConsumerInvokeHandler consumerInvokeHandler;


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest request) {
        try {
            ByteBuf content = request.content();
            if (content.isReadable()) {
                byte[] bytes = new byte[content.readableBytes()];
                content.readBytes(bytes);
                consumerInvokeHandler.invoke(channelHandlerContext, new String(bytes));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
