package com.stouduo.mesh.dubbo;

import com.stouduo.mesh.dubbo.model.Bytes;
import com.stouduo.mesh.dubbo.model.RpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.Arrays;
import java.util.List;

public class DubboRpcDecoder extends ByteToMessageDecoder {
    // header length.
    protected static final int HEADER_LENGTH = 16;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        list.add(decode2(byteBuf));
    }

    private Object decode2(ByteBuf byteBuf) {
        byte[] data = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(data);

        byte[] subArray = Arrays.copyOfRange(data, HEADER_LENGTH + 1, data.length);

        String s = new String(subArray);

        System.err.println(s);
        byte[] requestIdBytes = Arrays.copyOfRange(data, 4, 12);
        long requestId = Bytes.bytes2long(requestIdBytes, 0);

        RpcResponse response = new RpcResponse();
        response.setRequestId(String.valueOf(requestId));
        response.setBytes(subArray);
        return response;
    }
}
