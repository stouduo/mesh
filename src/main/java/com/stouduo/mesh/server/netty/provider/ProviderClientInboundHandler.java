package com.stouduo.mesh.server.netty.provider;

import com.alibaba.fastjson.JSON;
import com.stouduo.mesh.dubbo.model.RpcResponse;
import com.stouduo.mesh.server.netty.util.RequestHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ProviderClientInboundHandler extends SimpleChannelInboundHandler<RpcResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
        if (RequestHolder.getRequest(msg.getRequestId()) != 0) {
            ctx.writeAndFlush(JSON.parseObject((byte[]) msg.getBody(), Integer.class));
            RequestHolder.remove(msg.getRequestId());
        }
    }
}
