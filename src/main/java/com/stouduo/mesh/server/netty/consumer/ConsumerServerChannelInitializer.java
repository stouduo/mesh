package com.stouduo.mesh.server.netty.consumer;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ConsumerServerChannelInitializer extends ChannelInitializer {
    @Autowired
    private ConsumerServerInboundHandler consumerServerInboundHandler;
    @Autowired
    @Qualifier("bizWorker")
    private EventLoopGroup bizWorker;

    @Override
    protected void initChannel(Channel channel) throws Exception {
        channel.pipeline()
                .addLast(new HttpRequestDecoder())
                .addLast(new HttpObjectAggregator(65536))
                .addLast(new HttpContentCompressor())
                .addLast(new HttpResponseEncoder())
                .addLast(bizWorker,consumerServerInboundHandler);
    }
}
