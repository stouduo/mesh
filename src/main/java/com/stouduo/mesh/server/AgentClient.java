package com.stouduo.mesh.server;

import com.stouduo.mesh.dubbo.model.RpcDTO;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.pool.*;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.FutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class AgentClient implements AutoCloseable {
    protected static Logger logger = LoggerFactory.getLogger(AgentClient.class);
    @Autowired()
    @Qualifier("ioWorker")
    protected EventLoopGroup workerGroup;
    protected ChannelPoolMap<InetSocketAddress, SimpleChannelPool> poolMap;
    @Autowired
    protected ChannelPoolHandler clientChannelPoolHandler;
//    @Value("${agent.biz.threadpool.coreSize:5}")
//    protected int coreSize;
//    @Value("${agent.biz.threadpool.maxSize:50}")
//    protected int maxSize;
//    @Value("${agent.biz.threadpool.queueCapacity:32}")
//    protected int queueCapacity;
//    protected ThreadPoolExecutor bizWorkers;
    protected int maxChannels;

    @PostConstruct
    public void start() {
        Bootstrap bootstrap = new Bootstrap().group(workerGroup)
                .channel(System.getProperty("os.name").contains("Windows") ? NioSocketChannel.class : EpollSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true);
        poolMap = new AbstractChannelPoolMap<InetSocketAddress, SimpleChannelPool>() {
            @Override
            protected synchronized SimpleChannelPool newPool(InetSocketAddress key) {
                return new FixedChannelPool(bootstrap.remoteAddress(key), clientChannelPoolHandler, maxChannels);
            }
        };
//        this.bizWorkers = new ThreadPoolExecutor(coreSize, maxSize, 5, TimeUnit.MINUTES, new LinkedBlockingQueue<>(queueCapacity), new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    public void asycSend(InetSocketAddress remoteAddress, final Object data) {
        SimpleChannelPool pool = poolMap.get(remoteAddress);
        pool.acquire().addListener((FutureListener<Channel>) future -> {
            if (future.isSuccess()) {
                Channel channel = future.getNow();
                channel.writeAndFlush(data);
                pool.release(channel);
            }
        });
    }

    public abstract void invoke(RpcDTO data);

    @Override
    public void close() {
        this.workerGroup.shutdownGracefully();
    }

}
