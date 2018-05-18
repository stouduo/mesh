package com.stouduo.mesh.server.netty.provider;

import com.stouduo.mesh.dubbo.model.RpcResponse;
import com.stouduo.mesh.server.ServerChannelInitializer;
import com.stouduo.mesh.server.netty.util.CustomByteToMessageCodec;
import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

@Component
public class ProviderServerChannelInitializer extends ServerChannelInitializer {

    @Override
    protected void initChannel(Channel channel) throws Exception {
        channel.pipeline()
                .addLast(new CustomByteToMessageCodec(RpcResponse.class))
                .addLast(new ProviderServerInboundHandler());
    }
}
