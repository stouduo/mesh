package com.stouduo.mesh.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

public class AgentServer implements AutoCloseable, ApplicationRunner {
    private Logger logger = LoggerFactory.getLogger(AgentServer.class);
    @Value("${server.port}")
    private int port;
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    @Autowired
    private ChannelInitializer serverChannelInitializer;

    public AgentServer() {
        int workerCount = Runtime.getRuntime().availableProcessors();
        bossGroup = new NioEventLoopGroup(workerCount);
        workerGroup = new NioEventLoopGroup(workerCount);
    }

    public void start() {
        new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_BACKLOG, 1024)
//                .childOption(ChannelOption.SO_RCVBUF, 256 * 1024)
//                .option(ChannelOption.SO_RCVBUF, 256 * 1024)
//                .childOption(ChannelOption.SO_SNDBUF, 256 * 1024)
                .childHandler(serverChannelInitializer)
                .bind(port);
    }

    public void close() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info(">>>>>服务启动中...");
        start();
        logger.info(">>>>>服务已启动");
    }
}
