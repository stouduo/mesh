package com.stouduo.mesh.server.netty.provider;

import com.stouduo.mesh.dubbo.model.RpcDTO;
import com.stouduo.mesh.server.invoke.ProviderInvokeHandler;
import com.stouduo.mesh.server.netty.util.ContextHolder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Component
@ChannelHandler.Sharable
public class ProviderServerInboundHandler extends SimpleChannelInboundHandler<RpcDTO> {
//    private static ExecutorService executor = Executors.newFixedThreadPool(32);

    @Autowired
    private ProviderInvokeHandler providerInvokeHandler;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcDTO data) throws Exception {
        ContextHolder.putContext(data.getSessionId(), channelHandlerContext);
//        executor.execute(() -> providerInvokeHandler.invoke(data));
        providerInvokeHandler.invoke(data);
    }
}
