package com.stouduo.mesh.server.netty.consumer;

import com.stouduo.mesh.dubbo.model.RpcDTO;
import com.stouduo.mesh.server.AgentClient;
import com.stouduo.mesh.util.Endpoint;
import io.netty.channel.nio.NioEventLoopGroup;

import java.net.InetSocketAddress;

public class ConsumerAgentClient extends AgentClient {

    public ConsumerAgentClient(int maxChannels) {
        this.maxChannels = maxChannels;
        this.workerGroup = new NioEventLoopGroup(8);
        this.clientChannelPoolHandler = new ConsumerClientChannelPoolHandler();
    }

    @Override
    public void invoke(RpcDTO data) {
        Endpoint remoteServer = data.getRemoteServer();
        try {
//            bizWorkers.execute(() -> asycSend(new InetSocketAddress(remoteServer.getHost(), remoteServer.getPort()), data));
            asycSend(new InetSocketAddress(remoteServer.getHost(), remoteServer.getPort()), data);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
