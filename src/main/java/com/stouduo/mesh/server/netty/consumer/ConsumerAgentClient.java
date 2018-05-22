package com.stouduo.mesh.server.netty.consumer;

import com.stouduo.mesh.dubbo.model.RpcFuture;
import com.stouduo.mesh.dubbo.model.RpcRequestHolder;
import com.stouduo.mesh.rpc.RpcRequest;
import com.stouduo.mesh.server.AgentClient;
import com.stouduo.mesh.util.Endpoint;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.SimpleChannelPool;

import java.net.InetSocketAddress;

public class ConsumerAgentClient extends AgentClient {

    public ConsumerAgentClient(int maxChannels) {
        this.maxChannels = maxChannels;
        this.workerGroup = new NioEventLoopGroup();
    }

    @Override
    public Object invoke(RpcRequest rpcRequest) {
        Endpoint reomteServer = rpcRequest.getRemoteServer();
        try {
            SimpleChannelPool pool = poolMap.get(new InetSocketAddress(reomteServer.getHost(), reomteServer.getPort()));
            Channel channel = pool.acquire().get();
            RpcFuture future = new RpcFuture();
            RpcRequestHolder.put(String.valueOf(rpcRequest.getId()), future);
            channel.writeAndFlush(rpcRequest);
            pool.release(channel);
            return future.get();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }
}
