package com.stouduo.mesh.server.netty.provider;

import com.stouduo.mesh.dubbo.DubboRpcDecoder;
import com.stouduo.mesh.dubbo.DubboRpcEncoder;
import io.netty.channel.Channel;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.socket.SocketChannel;

public class ProviderClientChannelPoolHandler implements ChannelPoolHandler {
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
                .addLast(new DubboRpcEncoder())
                .addLast(new DubboRpcDecoder())
                .addLast(providerClientInboundHandler);
    }

    private ProviderClientInboundHandler providerClientInboundHandler = new ProviderClientInboundHandler();
}
