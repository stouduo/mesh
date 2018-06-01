package com.stouduo.mesh.server.netty.consumer;

import com.stouduo.mesh.dubbo.model.RpcDTO.*;
import com.stouduo.mesh.server.AgentClient;
import io.netty.channel.nio.NioEventLoopGroup;

import java.net.InetSocketAddress;

public class ConsumerAgentClient extends AgentClient {

    public ConsumerAgentClient(int maxChannels) {
        this.maxChannels = maxChannels;
        this.workerGroup = new NioEventLoopGroup(8);
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
