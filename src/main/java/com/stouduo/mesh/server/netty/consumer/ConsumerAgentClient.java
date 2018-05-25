package com.stouduo.mesh.server.netty.consumer;

import com.stouduo.mesh.rpc.RpcRequest;
import com.stouduo.mesh.server.AgentClient;
import com.stouduo.mesh.server.netty.util.ContextHolder;
import com.stouduo.mesh.util.Endpoint;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.util.concurrent.FutureListener;

import java.net.InetSocketAddress;

public class ConsumerAgentClient extends AgentClient {

    public ConsumerAgentClient(int maxChannels) {
        this.maxChannels = maxChannels;
        this.workerGroup = new NioEventLoopGroup();
    }

    @Override
    public void invoke(RpcRequest rpcRequest) {
        Endpoint remoteServer = rpcRequest.getRemoteServer();
        try {
            asycSend(new InetSocketAddress(remoteServer.getHost(), remoteServer.getPort()), rpcRequest);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
