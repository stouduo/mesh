package com.stouduo.mesh.dubbo.modify;

import com.stouduo.mesh.util.IpHelper;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class ChannelPoolManager {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(ChannelPoolManager.class);
    private final NioEventLoopGroup group = new NioEventLoopGroup(8);
    private final Bootstrap bs = new Bootstrap();
    private static FixedChannelPool channelPool = null;

    public ChannelPoolManager(int port, int maxChannels) {
        bs.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        try {
            channelPool = new FixedChannelPool(bs.remoteAddress(InetSocketAddress.createUnresolved(IpHelper.getHostIp(), port)), new CustomChannelPoolHandler(), maxChannels);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Future<Channel> acquireFuture() {
        return channelPool.acquire();
    }

    //申请连接，没有申请到(或者网络断开)，返回null
    public static Channel acquire(int seconds) {
        try {
            return channelPool.acquire().get(seconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    public static Channel acquire() {
        try {
            return channelPool.acquire().get();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    //释放连接
    public static void release(Channel channel) {
        try {
            if (channel != null) {
                channelPool.release(channel);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
