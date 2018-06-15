package com.stouduo.mesh.server.netty.consumer;

import com.alibaba.fastjson.JSON;
import com.stouduo.mesh.dubbo.model.RpcDTO;
import com.stouduo.mesh.server.netty.util.ContextHolder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
@ChannelHandler.Sharable
public class ConsumerClientInboundHandler extends SimpleChannelInboundHandler<RpcDTO> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcDTO data) throws Exception {
        ctx.channel().eventLoop().execute(()->{
            long sessionId = data.getSessionId();
            ChannelHandlerContext context = ContextHolder.getContext(sessionId);
            if (context != null) {
                FullHttpResponse response = new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                        Unpooled.wrappedBuffer(parseData(data)));
                response.headers().set(CONTENT_TYPE, "application/json")
                        .set(CONTENT_LENGTH, response.content().readableBytes())
                        .set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                context.writeAndFlush(response);
                ContextHolder.remove(sessionId);
            }
        });
    }

    private static byte[] errorBytes = "stouduo".getBytes();

    private byte[] parseData(RpcDTO data) {
        ByteBuf content = data.getContent();
        if (content.readableBytes() == errorBytes.length) {
            content.release();
            return errorBytes;
        }
        byte[] body = new byte[content.readableBytes() - 2];
        content.skipBytes(2);
        content.readBytes(body);
        content.release();
        return JSON.parseObject(body, String.class).toString().getBytes();
    }
}
