package com.stouduo.mesh.server.netty.consumer;

import com.alibaba.fastjson.JSON;
import com.stouduo.mesh.rpc.client.RpcRequest;
import com.stouduo.mesh.server.invoke.ConsumerInvokeHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.CharsetUtil;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;

public class ConsumerServerInboundHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest request) throws Exception {
        HttpMethod method = request.method();
        RpcRequest rpcRequest = new RpcRequest();
        MultiValueMap<String, String> parmMap = new LinkedMultiValueMap<>();
        if (method.equals(HttpMethod.GET)) {// 是GET请求
            QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
            parmMap.putAll(decoder.parameters());
        } else if (method.equals(HttpMethod.POST)) { // 是POST请求
            ByteBuf byteBuf = request.content();
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(request);
            if (byteBuf.isReadable()) {
                parmMap.putAll((Map<? extends String, ? extends List<String>>) JSON.parse(request.content().toString(CharsetUtil.UTF_8)));
            }
            decoder.offer(request);//form
            List<InterfaceHttpData> parmList = decoder.getBodyHttpDatas();
            for (InterfaceHttpData parm : parmList) {
                Attribute data = (Attribute) parm;
                parmMap.add(data.getName(), data.getValue());
            }
        } else {
            parmMap = null;
        }
        if (parmMap != null) {
            rpcRequest.setMultiParameters(parmMap);
            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer((byte[]) new ConsumerInvokeHandler().invoke(rpcRequest)));
            response.headers().add("content-type", "application/json");
            channelHandlerContext.writeAndFlush(request);
        }
    }
}
