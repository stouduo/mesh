package com.stouduo.mesh.server.netty.provider;

import com.stouduo.mesh.dubbo.model.RpcDTO;
import com.stouduo.mesh.server.netty.util.ContextHolder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
@ChannelHandler.Sharable
public class ProviderClientInboundHandler extends SimpleChannelInboundHandler<RpcDTO> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcDTO data) throws Exception {
        long sessionId = data.getSessionId();
        ChannelHandlerContext context = ContextHolder.getContext(sessionId);
        if (context != null) {
            context.writeAndFlush(data);
            ContextHolder.remove(sessionId);
        }
    }
}
