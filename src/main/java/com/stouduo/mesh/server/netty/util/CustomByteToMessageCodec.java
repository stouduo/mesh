package com.stouduo.mesh.server.netty.util;

import com.stouduo.mesh.dubbo.model.Bytes;
import com.stouduo.mesh.rpc.client.RpcRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.Arrays;
import java.util.List;

public class CustomByteToMessageCodec extends ByteToMessageCodec {
    private final static short PROTOCOL = (short) 0xabcd;
    private final static int HEADER_LENGTH = 16;
    private Class<?> clazz;

    public CustomByteToMessageCodec(Class<?> clazz) {
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

        int savedReaderIndex = byteBuf.readerIndex();
        int readable = byteBuf.readableBytes();

        if (readable < HEADER_LENGTH) {
            return CustomByteToMessageCodec.DecodeResult.NEED_MORE_INPUT;
        }

        byte[] header = new byte[HEADER_LENGTH];
        byteBuf.readBytes(header);
        byte[] dataLen = Arrays.copyOfRange(header, 12, 16);
        int len = Bytes.bytes2int(dataLen);
        int tt = len + HEADER_LENGTH;
        if (readable < tt) {
            return CustomByteToMessageCodec.DecodeResult.NEED_MORE_INPUT;
        }

        byteBuf.readerIndex(savedReaderIndex);
        byte[] data = new byte[tt];
        byteBuf.readBytes(data);

        byte[] body = Arrays.copyOfRange(data, HEADER_LENGTH + 1, data.length);
        return ProtoSerializeUtil.deserialize(body, clazz);
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        byte[] header = new byte[HEADER_LENGTH];
        Bytes.short2bytes(PROTOCOL, header);
        if (o instanceof RpcRequest) {
            Bytes.long2bytes(((RpcRequest) o).getId(), header, 4);
        }
        byte[] body = ProtoSerializeUtil.serialize(o);

        byteBuf.writeBytes(body);
        Bytes.int2bytes(body.length, header, 12);
        // write
        byteBuf.writeBytes(header); // write header.
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
