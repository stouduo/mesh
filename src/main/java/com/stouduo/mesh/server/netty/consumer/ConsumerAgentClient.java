package com.stouduo.mesh.server.netty.consumer;

import com.alibaba.fastjson.JSON;
import com.stouduo.mesh.dubbo.model.RpcFuture;
import com.stouduo.mesh.dubbo.model.RpcRequestHolder;
import com.stouduo.mesh.rpc.RpcRequest;
import com.stouduo.mesh.server.AgentClient;
import com.stouduo.mesh.server.netty.util.RequestHolder;
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
    public void invoke(RpcRequest rpcRequest) {
        Endpoint remoteServer = rpcRequest.getRemoteServer();
        try {
            SimpleChannelPool pool = poolMap.get(new InetSocketAddress(remoteServer.getHost(), remoteServer.getPort()));
            Channel channel = pool.acquire().get();
//            RpcFuture future = new RpcFuture();
//            RpcRequestHolder.put(String.valueOf(rpcRequest.getId()), future);
            RequestHolder.putRequest(rpcRequest.getId());
            channel.writeAndFlush(rpcRequest);
            pool.release(channel);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
