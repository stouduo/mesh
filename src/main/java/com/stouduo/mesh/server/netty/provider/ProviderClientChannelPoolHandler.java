package com.stouduo.mesh.server.netty.provider;

import com.stouduo.mesh.dubbo.DubboRpcDecoder;
import com.stouduo.mesh.dubbo.DubboRpcEncoder;
import com.stouduo.mesh.server.ClientInboundHandler;
import io.netty.channel.Channel;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ProviderClientChannelPoolHandler implements ChannelPoolHandler {
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
                .addLast(new DubboRpcEncoder())
                .addLast(new DubboRpcDecoder())
                .addLast(new ClientInboundHandler());
    }
}
