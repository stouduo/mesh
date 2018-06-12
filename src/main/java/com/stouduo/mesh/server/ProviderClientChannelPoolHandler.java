package com.stouduo.mesh.server;

import com.stouduo.mesh.dubbo.DubboRpcDecoder;
import com.stouduo.mesh.dubbo.DubboRpcEncoder;
import com.stouduo.mesh.dubbo.model.RpcDTO;
import com.stouduo.mesh.util.FutureHolder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.CompletableFuture;

public class ProviderClientChannelPoolHandler implements ChannelPoolHandler {
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
}
