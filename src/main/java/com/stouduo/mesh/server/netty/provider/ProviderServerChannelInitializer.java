package com.stouduo.mesh.server.netty.provider;

import com.stouduo.mesh.dubbo.model.RpcDTO;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import org.springframework.beans.factory.annotation.Autowired;

public class ProviderServerChannelInitializer extends ChannelInitializer {
    @Autowired
    private ProviderServerInboundHandler providerServerInboundHandler;

    @Override
    protected void initChannel(Channel channel) throws Exception {
        channel.pipeline()
//                .addLast(new CustomByteToMessageCodec(RpcRequest.class))
                .addLast(new ProtobufVarint32FrameDecoder())
                .addLast(new ProtobufDecoder(RpcDTO.RpcRequest.getDefaultInstance()))
                .addLast(new ProtobufVarint32LengthFieldPrepender())
                .addLast(new ProtobufEncoder())
                .addLast(providerServerInboundHandler);
    }
}
