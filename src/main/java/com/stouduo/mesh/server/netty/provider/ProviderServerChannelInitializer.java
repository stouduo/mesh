package com.stouduo.mesh.server.netty.provider;

import com.stouduo.mesh.server.netty.util.CustomByteToMessageCodec;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import org.springframework.beans.factory.annotation.Autowired;

public class ProviderServerChannelInitializer extends ChannelInitializer {
    @Autowired
    private ProviderServerInboundHandler providerServerInboundHandler;


    @Override
    protected void initChannel(Channel channel) throws Exception {
        channel.pipeline()
                .addLast(new HttpRequestDecoder())
                .addLast(new HttpObjectAggregator(65536))
                .addLast(new HttpContentCompressor())
                .addLast(new HttpResponseEncoder())
                .addLast(providerServerInboundHandler);
    }
}
