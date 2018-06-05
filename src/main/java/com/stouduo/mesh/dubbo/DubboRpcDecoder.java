package com.stouduo.mesh.dubbo;

import com.stouduo.mesh.dubbo.model.RpcDTO;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class DubboRpcDecoder extends ByteToMessageDecoder {
    // header length.
    protected static final int HEADER_LENGTH = 16;
    protected static final byte FLAG_EVENT = (byte) 0x20;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        byteBuf.markReaderIndex();
        int readable = byteBuf.readableBytes();
        if (readable < HEADER_LENGTH) return;
        byteBuf.skipBytes(4);
        long sessionId = byteBuf.readLong();
        int len = byteBuf.readInt();
        if (readable < len + HEADER_LENGTH) {
            byteBuf.resetReaderIndex();
            return;
        }
        int readIndex = byteBuf.readerIndex();
        byteBuf.readerIndex(readIndex + len);
        ByteBuf sendDirect = byteBuf.slice(readIndex, len);
        sendDirect.retain();
        list.add(new RpcDTO().setSessionId(sessionId).setContent(sendDirect));
    }

}
