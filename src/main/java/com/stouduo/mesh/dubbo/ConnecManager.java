package com.stouduo.mesh.dubbo;

import com.stouduo.mesh.util.Endpoint;
import com.stouduo.mesh.util.IpHelper;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ConnecManager {
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);

    private volatile Bootstrap bootstrap;

    private volatile Channel channel;
    private Object lock = new Object();
    private Endpoint connectUri;

    public ConnecManager(Endpoint connectUri) {
        this.connectUri = connectUri;
    }

    public Channel getChannel() throws Exception {
        if (null != channel) {
            return channel;
        }
        if (null == bootstrap) {
            synchronized (lock) {
                if (null == bootstrap) {
                    initBootstrap();
                }
            }
        }

        if (null == channel) {
            synchronized (lock) {
                if (null == channel) {
                    channel = bootstrap.connect(connectUri.getHost(), connectUri.getPort()).sync().channel();
                }
            }
        }

        return channel;
    }

    public void initBootstrap() {

        bootstrap = new Bootstrap()
                .group(eventLoopGroup)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT)
                .channel(NioSocketChannel.class)
                .handler(new RpcClientInitializer());

    }
}
