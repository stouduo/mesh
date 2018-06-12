package com.stouduo.mesh.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

public class AgentServer implements AutoCloseable, ApplicationRunner {
    private Logger logger = LoggerFactory.getLogger(AgentServer.class);
    @Value("${server.port}")
    private int port;
    //    @Autowired
//    @Qualifier("boss")
//    protected EventLoopGroup bossGroup;
    @Autowired
    @Qualifier("ioWorker")
    protected EventLoopGroup workerGroup;
    @Autowired
    private ChannelInitializer serverChannelInitializer;

    public void start() {
        new ServerBootstrap()
                .group(workerGroup)
                .channel(System.getProperty("os.name").contains("Windows") ? NioServerSocketChannel.class : EpollServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(serverChannelInitializer)
                .bind(port);
    }

    public void close() {
//        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        start();
    }
}
