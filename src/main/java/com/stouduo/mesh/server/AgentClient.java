package com.stouduo.mesh.server;

import com.stouduo.mesh.rpc.RpcRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public abstract class AgentClient implements AutoCloseable {
    protected static Logger logger = LoggerFactory.getLogger(AgentClient.class);
    protected NioEventLoopGroup workerGroup;
    protected InetSocketAddress inetSocketAddress;
    protected ChannelPoolMap<InetSocketAddress, SimpleChannelPool> poolMap;
    @Autowired
    protected ChannelPoolHandler clientChannelPoolHandler;

    protected int maxChannels;

    @PostConstruct
    public void start() {
        Bootstrap bootstrap = new Bootstrap().group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT);
        poolMap = new AbstractChannelPoolMap<InetSocketAddress, SimpleChannelPool>() {
            @Override
            protected SimpleChannelPool newPool(InetSocketAddress key) {
                return new FixedChannelPool(bootstrap.remoteAddress(key), clientChannelPoolHandler, maxChannels);
            }
        };
    }

    public abstract Object invoke(RpcRequest rpcRequest);

    @Override
    public void close() {
        this.workerGroup.shutdownGracefully();
    }

}
