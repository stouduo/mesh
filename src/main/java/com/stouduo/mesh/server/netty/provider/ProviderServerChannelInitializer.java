package com.stouduo.mesh.server.netty.provider;

import com.stouduo.mesh.rpc.RpcRequest;
import com.stouduo.mesh.server.netty.util.CustomByteToMessageCodec;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import org.springframework.beans.factory.annotation.Autowired;

public class ProviderServerChannelInitializer extends ChannelInitializer {
    @Autowired
    private ProviderServerInboundHandler providerServerInboundHandler;

    @Override
    protected void initChannel(Channel channel) throws Exception {
        channel.pipeline()
                .addLast(new CustomByteToMessageCodec(RpcRequest.class))
                .addLast(providerServerInboundHandler);
    }
}