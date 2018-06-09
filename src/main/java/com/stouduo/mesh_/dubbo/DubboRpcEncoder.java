package com.stouduo.mesh_.dubbo;

import com.stouduo.mesh_.dubbo.model.Request;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class DubboRpcEncoder extends MessageToByteEncoder<Request> {
    // header length.
    protected static final int HEADER_LENGTH = 16;
    // magic header.
    protected static final short MAGIC = (short) 0xdabb;
    // message flag.
    protected static final byte FLAG_REQUEST = (byte) 0x80;
    protected static final byte FLAG_TWOWAY = (byte) 0x40;
    protected static final byte FLAG_EVENT = (byte) 0x20;

    @Override
    protected void encode(ChannelHandlerContext ctx, Request request, ByteBuf buffer) throws Exception {
        buffer.writeShort(MAGIC);
        buffer.writeByte(FLAG_REQUEST | 6 | FLAG_TWOWAY);
        buffer.writerIndex(buffer.writerIndex() + 1);
        buffer.writeLong(request.getId());
        byte[] data = (byte[]) request.getData();
        buffer.writeInt(data.length);
        buffer.writeBytes(data);
    }

}
