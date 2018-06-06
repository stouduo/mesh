package com.stouduo.mesh.server.invoke;

import com.stouduo.mesh.registry.IRegistry;
import com.stouduo.mesh.rpc.loadbalance.strategy.ILbStrategy;
import com.stouduo.mesh.util.Endpoint;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.asynchttpclient.Dsl.asyncHttpClient;
import static org.asynchttpclient.Dsl.post;

@Component
public class ConsumerInvokeHandler {
    private static Logger logger = LoggerFactory.getLogger(ConsumerInvokeHandler.class);

    @Autowired
    private ILbStrategy iLbStrategy;
    @Autowired
    private IRegistry iRegistry;
    private AsyncHttpClient agentClient = asyncHttpClient();

    @Value("${agent.provider.serviceName}")
    private String serviceName;

    @Value("${agent.biz.threadpool.coreSize:5}")
    protected int coreSize;
    @Value("${agent.biz.threadpool.maxSize:50}")
    protected int maxSize;
    @Value("${agent.biz.threadpool.queueCapacity:32}")
    protected int queueCapacity;
    protected ThreadPoolExecutor bizWorkers;

    @PostConstruct
    public void initBizWokers() {
        this.bizWorkers = new ThreadPoolExecutor(coreSize, maxSize, 5, TimeUnit.MINUTES, new LinkedBlockingQueue<>(queueCapacity), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public void invoke(ChannelHandlerContext context, String data) {
        bizWorkers.execute(() -> {
            try {
                Endpoint endpoint = iLbStrategy.lbStrategy(iRegistry.find(serviceName));
                ListenableFuture<Response> responseFuture = agentClient.executeRequest(post(new StringBuilder("http://")
                        .append(endpoint.getHost())
                        .append(":")
                        .append(endpoint.getPort())
                        .toString())
                        .setBody(data)
                        .addHeader("ContentType", "text/plain")
                        .build());
                responseFuture.addListener(() -> {
                    try {
                        Response respo = responseFuture.get();
                        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(respo.getResponseBody().getBytes()));
                        fullHttpResponse.headers().add(respo.getHeaders());
                        context.writeAndFlush(fullHttpResponse);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
