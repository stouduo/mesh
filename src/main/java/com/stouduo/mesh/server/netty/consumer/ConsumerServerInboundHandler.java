package com.stouduo.mesh.server.netty.consumer;

import com.stouduo.mesh.dubbo.model.RpcDTO;
import com.stouduo.mesh.server.invoke.Invoker;
import com.stouduo.mesh.server.netty.util.ContextHolder;
import com.stouduo.mesh.server.netty.util.DisruptorHolder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
public class ConsumerServerInboundHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
//    @Autowired
//    private DisruptorHolder disruptorHolder;

    @Autowired
    private Invoker invoker;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest request) {
        try {
            ByteBuf content = request.content();
            if (content.isReadable()) {
                RpcDTO data = new RpcDTO().setContent(content.retain());
                ContextHolder.putContext(data.getSessionId(), channelHandlerContext);
//                disruptorHolder.getDisruptor().getRingBuffer().publishEvent((event, sequence, buffer) -> event.setRpcDTO(data));
//                channelHandlerContext.channel().eventLoop().execute(() -> invoker.invoke(data));
                invoker.invoke(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
