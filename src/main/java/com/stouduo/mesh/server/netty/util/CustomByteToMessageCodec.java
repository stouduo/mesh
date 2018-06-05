package com.stouduo.mesh.server.netty.util;

import com.stouduo.mesh.dubbo.model.RpcDTO;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CustomByteToMessageCodec extends ByteToMessageCodec<RpcDTO> {
    private static Logger logger = LoggerFactory.getLogger(CustomByteToMessageCodec.class);

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcDTO data, ByteBuf byteBuf) throws Exception {
        try {
            ByteBuf content = data.getContent();
            byteBuf.writeBoolean(data.isError());
            byteBuf.writeInt(content.readableBytes());
            byteBuf.writeLong(data.getSessionId());
            byteBuf.writeBytes(content);
            content.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List list) throws Exception {
        byteBuf.markReaderIndex();
        int readable = byteBuf.readableBytes();
        if (readable < 5) return;
        boolean isError = byteBuf.readBoolean();
        int len = byteBuf.readInt();
        if (readable < len + 13) {
            byteBuf.resetReaderIndex();
            return;
        }
        long sessionId = byteBuf.readLong();
        int readIndex = byteBuf.readerIndex();
        byteBuf.readerIndex(readIndex + len);
        ByteBuf sendDirect = byteBuf.slice(readIndex, len);
        sendDirect.retain();
        list.add(new RpcDTO().setError(isError).setSessionId(sessionId).setContent(sendDirect));
    }
}
