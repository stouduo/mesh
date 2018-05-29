package com.stouduo.mesh.server.netty.consumer;

import com.stouduo.mesh.dubbo.model.RpcResponse;
import com.stouduo.mesh.server.netty.util.CustomByteToMessageCodec;
import io.netty.channel.Channel;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ConsumerClientChannelPoolHandler implements ChannelPoolHandler {
    private static EventLoopGroup workers = new DefaultEventLoopGroup();

    @Override
    public void channelReleased(Channel channel) throws Exception {

    }

    @Override
    public void channelAcquired(Channel channel) throws Exception {

    }

    @Override
    public void channelCreated(Channel ch) throws Exception {
        NioSocketChannel channel = (NioSocketChannel) ch;
        channel.config().setKeepAlive(true);
        channel.config().setTcpNoDelay(true);
        channel.pipeline()
                .addLast(new CustomByteToMessageCodec(RpcResponse.class))
                .addLast(workers, new ConsumerClientInboundHandler());
    }
}
