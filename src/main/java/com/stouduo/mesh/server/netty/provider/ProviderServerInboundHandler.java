package com.stouduo.mesh.server.netty.provider;

import com.stouduo.mesh.dubbo.model.RpcDTO;
import com.stouduo.mesh.server.invoke.ProviderInvokeHandler;
import com.stouduo.mesh.server.netty.util.ContextHolder;
import com.stouduo.mesh.server.netty.util.DisruptorHolder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Component
@ChannelHandler.Sharable
public class ProviderServerInboundHandler extends SimpleChannelInboundHandler<RpcDTO> {
    @Autowired
    private DisruptorHolder disruptorHolder;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcDTO data) throws Exception {
        ContextHolder.putContext(data.getSessionId(), channelHandlerContext);
        disruptorHolder.getDisruptor().getRingBuffer().publishEvent((event, sequence, buffer) -> event.setRpcDTO(data));
    }
}
