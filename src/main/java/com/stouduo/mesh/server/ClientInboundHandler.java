package com.stouduo.mesh.server;

import com.stouduo.mesh.dubbo.model.RpcFuture;
import com.stouduo.mesh.dubbo.model.RpcRequestHolder;
import com.stouduo.mesh.dubbo.model.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientInboundHandler extends SimpleChannelInboundHandler<RpcResponse> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse response) throws Exception {
        System.out.println(response.toString());
        String requestId = response.getRequestId();
        RpcFuture future = RpcRequestHolder.get(requestId);
        if (null != future) {
            RpcRequestHolder.remove(requestId);
            future.done(response);
        }
    }
}
