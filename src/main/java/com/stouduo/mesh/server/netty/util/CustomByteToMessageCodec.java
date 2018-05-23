package com.stouduo.mesh.server.netty.util;

import com.stouduo.mesh.dubbo.model.Bytes;
import com.stouduo.mesh.dubbo.model.RpcResponse;
import com.stouduo.mesh.rpc.RpcRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.xml.BeanDefinitionDocumentReader;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Base64;
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
        byte[] header = new byte[HEADER_LENGTH];
        byteBuf.readBytes(header);
        int len = Bytes.bytes2int(Arrays.copyOfRange(header, 4, 8));
        if (readable < len + HEADER_LENGTH) {
            return CustomByteToMessageCodec.DecodeResult.NEED_MORE_INPUT;
        }
        byte[] body = new byte[len];
        byteBuf.readBytes(body);
        Object ret = SerializeUtil.deserialize(body, clazz);
//        if (clazz.equals(RpcResponse.class)) {
//            ((RpcResponse) ret).setRequestId(Bytes.bytes2long(Arrays.copyOfRange(header, 4, 12), 0) + "");
//        }
        return ret;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        try {
            byte[] header = new byte[HEADER_LENGTH];
            Bytes.short2bytes(PROTOCOL, header);
//            if (o instanceof RpcRequest) {
//                Bytes.long2bytes(((RpcRequest) o).getId(), header, 4);
//            }
            byte[] body = SerializeUtil.serialize(o);
            Bytes.int2bytes(body.length, header, 4);
            byteBuf.writeBytes(header); // write header.
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
