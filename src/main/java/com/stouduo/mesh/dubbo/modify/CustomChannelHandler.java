package com.stouduo.mesh.dubbo.modify;

import com.stouduo.mesh.dubbo.model.RpcFuture;
import com.stouduo.mesh.dubbo.model.RpcRequestHolder;
import com.stouduo.mesh.dubbo.model.RpcResponse;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@Sharable
public class CustomChannelHandler extends SimpleChannelInboundHandler<RpcResponse> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse response) throws Exception {
//        String requestId = response.getRequestId();
//        RpcFuture future = RpcRequestHolder.get(requestId);
//        if (null != future) {
//            RpcRequestHolder.remove(requestId);
//            future.done(response);
////            ChannelPoolManager.release(channelHandlerContext.channel());
//        }
    }
}
