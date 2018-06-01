package com.stouduo.mesh.server.netty.provider;

import com.stouduo.mesh.dubbo.model.RpcDTO.RpcRequest;
import com.stouduo.mesh.server.invoke.ProviderInvokeHandler;
import com.stouduo.mesh.server.netty.util.ContextHolder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
public class ProviderServerInboundHandler extends SimpleChannelInboundHandler<RpcRequest> {

    @Autowired
    private ProviderInvokeHandler providerInvokeHandler;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
        ContextHolder.putContext(rpcRequest.getId(), channelHandlerContext);
        providerInvokeHandler.invoke(rpcRequest);
    }
}