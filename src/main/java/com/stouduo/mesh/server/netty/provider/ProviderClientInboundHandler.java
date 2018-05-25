package com.stouduo.mesh.server.netty.provider;

import com.alibaba.fastjson.JSON;
import com.stouduo.mesh.dubbo.model.RpcResponse;
import com.stouduo.mesh.server.netty.util.ContextHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ProviderClientInboundHandler extends SimpleChannelInboundHandler<RpcResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
        long reqId = msg.getRequestId();
        ChannelHandlerContext context = ContextHolder.getContext(reqId);
        if (context != null) {
            RpcResponse response = new RpcResponse();
            response.setBody(JSON.parseObject((byte[]) msg.getBody(), Integer.class));
            response.setRequestId(reqId);
            context.writeAndFlush(response);
            ContextHolder.remove(reqId);
        }
    }
}
