package com.stouduo.mesh.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

public class AgentServer implements AutoCloseable {
    private int port;
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    @Autowired
    private ServerChannelInitializer serverChannelInitializer;

    public AgentServer(int port) {
        this.port = port;
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
    }

    @PostConstruct
    public void start() {
        new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT)
                .channel(NioServerSocketChannel.class)
                .handler(serverChannelInitializer)
                .bind(port);
    }

    public void close() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

}
