package com.stouduo.mesh.server.netty.provider;

import com.stouduo.mesh.server.netty.util.CustomByteToMessageCodec;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ProviderServerChannelInitializer extends ChannelInitializer {
    @Autowired
    private ProviderServerInboundHandler providerServerInboundHandler;
    @Autowired
    @Qualifier("bizWorker")
    private EventLoopGroup bizWorker;


    @Override
    protected void initChannel(Channel channel) throws Exception {
        channel.pipeline()
                .addLast(new CustomByteToMessageCodec())
                .addLast(bizWorker, providerServerInboundHandler);
    }
}
