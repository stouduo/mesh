package com.stouduo.mesh.dubbo;

import com.stouduo.mesh.dubbo.model.JsonUtils;
import com.stouduo.mesh.dubbo.model.Request;
import com.stouduo.mesh.dubbo.model.RpcDTO;
import com.stouduo.mesh.dubbo.model.RpcInvocation;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.UrlEncoded;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Map;

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
        System.out.println("encoder");
        buffer.writeShort(MAGIC);
        buffer.writeByte(FLAG_REQUEST | 6 | FLAG_TWOWAY);
        buffer.writerIndex(buffer.writerIndex() + 1);
        buffer.writeLong(request.getId());
        byte[] data = (byte[]) request.getData();
        buffer.writeInt(data.length);
        buffer.writeBytes(data);
    }

}
