package com.stouduo.mesh.server.netty.consumer;

import com.stouduo.mesh.server.netty.util.CustomByteToMessageCodec;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.socket.SocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ConsumerClientChannelPoolHandler implements ChannelPoolHandler {
    @Override
    public void channelReleased(Channel channel) throws Exception {

    }

    @Override
    public void channelAcquired(Channel channel) throws Exception {

    }

    @Override
    public void channelCreated(Channel ch) throws Exception {
        SocketChannel channel = (SocketChannel) ch;
        channel.config().setKeepAlive(true);
        channel.config().setTcpNoDelay(true);
        channel.pipeline()
                .addLast(new CustomByteToMessageCodec())
                .addLast(bizWorker, consumerClientInboundHandler);
    }

    private ConsumerClientInboundHandler consumerClientInboundHandler = new ConsumerClientInboundHandler();
    @Autowired
    @Qualifier("bizWorker")
    private EventLoopGroup bizWorker;
}
