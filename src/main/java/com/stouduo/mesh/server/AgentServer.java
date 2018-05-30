package com.stouduo.mesh.server;

import io.netty.bootstrap.ServerBootstrap;
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
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup(4);
    }

    public void start() {
        new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT)
                .channel(NioServerSocketChannel.class)
//                .handler(new LoggingHandler(LogLevel.WARN))
                .childOption(ChannelOption.TCP_NODELAY, true)
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
