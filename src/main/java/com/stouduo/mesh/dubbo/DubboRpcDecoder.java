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
        System.out.println("decoder");
        byteBuf.markReaderIndex();
        int readable = byteBuf.readableBytes();
        if (readable < HEADER_LENGTH) return;
        byteBuf.skipBytes(3);
        byte status = byteBuf.readByte();
        if (status == 0x64) {
            list.add("stouduo");
            return;
        }
        long sessionId = byteBuf.readLong();
        int len = byteBuf.readInt();
        System.out.println(readable + "---" + (len + HEADER_LENGTH));
        if (readable < len + HEADER_LENGTH) {
            byteBuf.resetReaderIndex();
            return;
        }
//        int readIndex = byteBuf.readerIndex();
//        byteBuf.readerIndex(readIndex + len);
        byte[] data = new byte[len - 2];
        byteBuf.skipBytes(2);
        byteBuf.readBytes(data);
        System.out.println(new String(data));
//        ByteBuf sendDirect = byteBuf.slice(readIndex, len);
//        sendDirect.retain();
        list.add(JSON.parseObject(data, String.class));
    }

}
