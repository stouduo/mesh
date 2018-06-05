package com.stouduo.mesh.server.netty.consumer;

import com.stouduo.mesh.dubbo.model.RpcDTO;
import com.stouduo.mesh.server.invoke.ConsumerInvokeHandler;
import com.stouduo.mesh.server.netty.util.ContextHolder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@ChannelHandler.Sharable
public class ConsumerServerInboundHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    @Autowired
    private ConsumerInvokeHandler consumerInvokeHandler;
    private static ExecutorService executor = Executors.newFixedThreadPool(32);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest request) {
        try {
            ByteBuf content = request.content();
            if (content.isReadable()) {
                RpcDTO data = new RpcDTO().setContent(content.retain());
                ContextHolder.putContext(data.getSessionId(), channelHandlerContext);
                executor.execute(() -> consumerInvokeHandler.invoke(data));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
