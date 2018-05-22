package com.stouduo.mesh.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

public class AgentServer implements AutoCloseable, ApplicationRunner {
    @Value("${server.port}")
    private int port;
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    @Autowired
    private ChannelInitializer serverChannelInitializer;

    public AgentServer() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
    }

    @PostConstruct
    public void start() {
        new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT)
                .channel(NioServerSocketChannel.class)
                .childHandler(serverChannelInitializer)
                .bind(port);
    }

    public void close() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        start();
    }
}
