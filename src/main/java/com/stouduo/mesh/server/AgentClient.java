package com.stouduo.mesh.server;

import com.stouduo.mesh.rpc.client.RpcRequest;
import com.stouduo.mesh.util.IpHelper;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

public class AgentClient implements AutoCloseable {
    private static Logger logger = LoggerFactory.getLogger(AgentClient.class);
    private final NioEventLoopGroup workerGroup;
    private static FixedChannelPool channelPool;
    private final int maxChannels;
    private final int serverPort;
    @Autowired
    private ClientChannelPoolHandler clientChannelPoolHandler;

    public AgentClient(int serverPort, int maxChannels) {
        this.maxChannels = maxChannels;
        this.serverPort = serverPort;
        this.workerGroup = new NioEventLoopGroup();
    }

    @PostConstruct
    public void start() {
        try {
            channelPool = new FixedChannelPool(new Bootstrap().group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT)
                    .remoteAddress(IpHelper.getHostIp(), serverPort), clientChannelPoolHandler, maxChannels);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object invoke(RpcRequest rpcRequest) {

        return null;
    }

    @Override
    public void close() {
        this.workerGroup.shutdownGracefully();
    }

}
