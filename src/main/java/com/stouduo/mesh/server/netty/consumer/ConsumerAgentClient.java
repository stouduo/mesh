package com.stouduo.mesh.server.netty.consumer;

import com.stouduo.mesh.dubbo.model.RpcDTO;
import com.stouduo.mesh.server.AgentClient;
import com.stouduo.mesh.util.Endpoint;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.netty.channel.nio.NioEventLoopGroup;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.time.Duration;

public class ConsumerAgentClient extends AgentClient {


    public ConsumerAgentClient(int maxChannels) {
        this.maxChannels = maxChannels;
        this.workerGroup = new NioEventLoopGroup();
        initRateLimiter();
    }

    private RateLimiter rateLimiter;

    private void initRateLimiter() {
        this.rateLimiter = RateLimiter.of("mesh", RateLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(61))
                .limitRefreshPeriod(Duration.ofSeconds(1))
                .limitForPeriod(5000)
                .build());
    }

    @Override
    public void invoke(RpcDTO data) {
        Endpoint remoteServer = data.getRemoteServer();
        try {
            bizWorkers.execute(RateLimiter.decorateRunnable(rateLimiter, () -> asycSend(new InetSocketAddress(remoteServer.getHost(), remoteServer.getPort()), data)));
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
