package com.stouduo.mesh.server;

import com.stouduo.mesh.dubbo.DubboRpcDecoder;
import com.stouduo.mesh.dubbo.DubboRpcEncoder;
import com.stouduo.mesh.dubbo.model.RpcDTO;
import com.stouduo.mesh.rpc.RpcRequest;
import com.stouduo.mesh.util.FutureHolder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class AgentClient implements AutoCloseable {
    protected static Logger logger = LoggerFactory.getLogger(AgentClient.class);
    protected NioEventLoopGroup workerGroup;
    protected ChannelPoolMap<InetSocketAddress, SimpleChannelPool> poolMap;
    protected int maxChannels;

    @PostConstruct
    public void start() {
        Bootstrap bootstrap = new Bootstrap().group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true);
        poolMap = new AbstractChannelPoolMap<InetSocketAddress, SimpleChannelPool>() {
            @Override
            protected synchronized SimpleChannelPool newPool(InetSocketAddress key) {
                return new FixedChannelPool(bootstrap.remoteAddress(key), new ChannelPoolHandler() {
                    @Override
                    public void channelReleased(Channel channel) throws Exception {
                    }
                    @Override
                    public void channelAcquired(Channel channel) throws Exception {
                    }
                    @Override
                    public void channelCreated(Channel ch) throws Exception {
                        NioSocketChannel channel = (NioSocketChannel) ch;
                        channel.config().setKeepAlive(true);
                        channel.config().setTcpNoDelay(true);
                        channel.pipeline()
                                .addLast(new DubboRpcEncoder())
                                .addLast(new DubboRpcDecoder())
                                .addLast(new SimpleChannelInboundHandler<RpcDTO>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcDTO data) throws Exception {
                                        long sessionId = data.getSessionId();
                                        CompletableFuture<String> future = FutureHolder.get(sessionId);
                                        if (future != null) {
                                            future.complete((String) data.getContent());
                                            FutureHolder.remove(sessionId);
                                        }
                                    }
                                });
                    }
                }, maxChannels);
            }
        };
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

    public abstract CompletableFuture<String> invoke(Object data);

    @Override
    public void close() {
        this.workerGroup.shutdownGracefully();
    }

}
