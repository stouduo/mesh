package com.stouduo.mesh.dubbo;

import com.alibaba.fastjson.JSON;
import com.google.gson.JsonDeserializer;
import com.stouduo.mesh.dubbo.model.JsonUtils;
import com.stouduo.mesh.dubbo.model.RpcDTO;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
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
        byteBuf.skipBytes(3);
        byte status = byteBuf.readByte();
        long sessionId = byteBuf.readLong();
        int len = byteBuf.readInt();
        if (readable < len + HEADER_LENGTH) {
            byteBuf.resetReaderIndex();
            return;
        }
        int readIndex = byteBuf.readerIndex();
        if (status == 0x64) {
            byteBuf.readerIndex(readIndex + len);
            list.add(new RpcDTO().setSessionId(sessionId).setContent("stouduo"));
            return;
        }
        byte[] content = new byte[byteBuf.readableBytes() - 2];
        byteBuf.skipBytes(1);
        byteBuf.readBytes(content);
        list.add(new RpcDTO().setSessionId(sessionId).setContent(JSON.parseObject(content, String.class)));
    }

}
