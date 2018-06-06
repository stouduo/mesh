package com.stouduo.mesh.server.netty.provider;

import com.stouduo.mesh.dubbo.model.RpcDTO;
import com.stouduo.mesh.server.invoke.ProviderInvokeHandler;
import com.stouduo.mesh.server.netty.util.ContextHolder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Component
@ChannelHandler.Sharable
public class ProviderServerInboundHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
//    private static ExecutorService executor = Executors.newFixedThreadPool(32);

    @Autowired
    private ProviderInvokeHandler providerInvokeHandler;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest request) throws Exception {
        try {
            ByteBuf content = request.content();
            if (content.isReadable()) {
                RpcDTO rpcDTO = new RpcDTO().setContent(content.retain());
                ContextHolder.putContext(rpcDTO.getSessionId(), channelHandlerContext);
                providerInvokeHandler.invoke(rpcDTO);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
