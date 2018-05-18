package com.stouduo.mesh.server.netty.consumer;

import com.stouduo.mesh.rpc.client.RpcRequest;
import com.stouduo.mesh.server.ClientChannelPoolHandler;
import com.stouduo.mesh.server.netty.util.CustomByteToMessageCodec;
import io.netty.channel.Channel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.stereotype.Component;

@Component
public class ConsumerClientChannelPoolHandler implements ClientChannelPoolHandler {
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
                .addLast(new CustomByteToMessageCodec(RpcRequest.class))
                .addLast(new ConsumerClientInboundHandler());
    }
}
