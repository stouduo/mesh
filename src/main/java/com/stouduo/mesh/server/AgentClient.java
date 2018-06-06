package com.stouduo.mesh.server;

import com.stouduo.mesh.dubbo.model.RpcDTO;
import com.stouduo.mesh.rpc.RpcRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.FutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class AgentClient implements AutoCloseable {
    protected static Logger logger = LoggerFactory.getLogger(AgentClient.class);
    protected NioEventLoopGroup workerGroup;
    protected ChannelPoolMap<InetSocketAddress, SimpleChannelPool> poolMap;
    @Autowired
    protected ChannelPoolHandler clientChannelPoolHandler;
    @Value("${agent.biz.threadpool.coreSize:5}")
    protected int coreSize;
    @Value("${agent.biz.threadpool.maxSize:50}")
    protected int maxSize;
    @Value("${agent.biz.threadpool.queueCapacity:32}")
    protected int queueCapacity;
    protected ThreadPoolExecutor bizWorkers;
    protected int maxChannels;

    @PostConstruct
    public void start() {
        Bootstrap bootstrap = new Bootstrap().group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
//                .option(ChannelOption.SO_RCVBUF, 256 * 1024)
//                .option(ChannelOption.SO_SNDBUF, 256 * 1024)
                .option(ChannelOption.TCP_NODELAY, true);
        poolMap = new AbstractChannelPoolMap<InetSocketAddress, SimpleChannelPool>() {
            @Override
            protected synchronized SimpleChannelPool newPool(InetSocketAddress key) {
//                logger.info(key.toString());
                return new FixedChannelPool(bootstrap.remoteAddress(key), clientChannelPoolHandler, maxChannels);
            }
        };
        this.bizWorkers = new ThreadPoolExecutor(coreSize, maxSize, 5, TimeUnit.MINUTES, new LinkedBlockingQueue<>(queueCapacity), new ThreadPoolExecutor.CallerRunsPolicy());
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
