package com.stouduo.mesh.server.netty.consumer;

import com.stouduo.mesh.server.ServerChannelInitializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import org.springframework.stereotype.Component;

@Component
public class ConsumerServerChannelInitializer extends ServerChannelInitializer {

    @Override
    protected void initChannel(Channel channel) throws Exception {
        channel.pipeline()
                .addLast(new HttpRequestDecoder())
                .addLast(new HttpObjectAggregator(65536))
                .addLast(new HttpContentCompressor())
                .addLast(new HttpResponseEncoder())
                .addLast(new ConsumerServerInboundHandler());
    }
}
