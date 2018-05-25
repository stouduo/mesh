package com.stouduo.mesh.server.netty.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;

public class CustomByteToMessageCodec<T extends Serializable> extends ByteToMessageCodec {
    private static Logger logger = LoggerFactory.getLogger(CustomByteToMessageCodec.class);
    private final static short PROTOCOL = (short) 0xabcd;
    private final static int HEADER_LENGTH = 8;
    private Class<T> clazz;

    public CustomByteToMessageCodec(Class<T> clazz) {
        this.clazz = clazz;
    }

    enum DecodeResult {
        NEED_MORE_INPUT, SKIP_INPUT
    }

    /**
     * Demo为简单起见，直接从特定字节位开始读取了的返回值，demo未做：
     * 1. 请求头判断
     * 2. 返回值类型判断
     *
     * @param byteBuf
     * @return
     */
    private Object decode2(ByteBuf byteBuf) {
        int readable = byteBuf.readableBytes();
        if (readable < HEADER_LENGTH) {
            return CustomByteToMessageCodec.DecodeResult.NEED_MORE_INPUT;
        }
        byteBuf.readerIndex(byteBuf.readerIndex() + 4);
        int len = byteBuf.readInt();
        if (readable < len + HEADER_LENGTH) {
            return CustomByteToMessageCodec.DecodeResult.NEED_MORE_INPUT;
        }
        byte[] body = new byte[len];
        byteBuf.readBytes(body);
        return SerializeUtil.deserialize(body, clazz);
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        try {
            byteBuf.writeShort(PROTOCOL);
            byteBuf.writeShort(0);
            byte[] body = SerializeUtil.serialize(o);
            byteBuf.writeInt(body.length);
            byteBuf.writeBytes(body);
        } catch (Exception e) {
            e.printStackTrace();
//            logger.error(e.getMessage());
        }
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List list) throws Exception {
        try {
            do {
                int savedReaderIndex = byteBuf.readerIndex();
                Object msg = null;
                try {
                    msg = decode2(byteBuf);
                } catch (Exception e) {
                    throw e;
                }
                if (msg == CustomByteToMessageCodec.DecodeResult.NEED_MORE_INPUT) {
                    byteBuf.readerIndex(savedReaderIndex);
                    break;
                }

                list.add(msg);
            } while (byteBuf.isReadable());
        } finally {
            if (byteBuf.isReadable()) {
                byteBuf.discardReadBytes();
            }
        }
    }
}
