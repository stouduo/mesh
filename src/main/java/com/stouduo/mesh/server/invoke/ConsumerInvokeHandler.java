package com.stouduo.mesh.server.invoke;

import com.stouduo.mesh.registry.IRegistry;
import com.stouduo.mesh.rpc.loadbalance.strategy.ILbStrategy;
import com.stouduo.mesh.util.Endpoint;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.RequestBuilder;
import org.asynchttpclient.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;

import static org.asynchttpclient.Dsl.asyncHttpClient;
import static org.asynchttpclient.Dsl.post;

public class ConsumerInvokeHandler implements Invoker {
    private static Logger logger = LoggerFactory.getLogger(ConsumerInvokeHandler.class);

    @Autowired
    private ILbStrategy iLbStrategy;
    @Autowired
    private IRegistry iRegistry;
    private AsyncHttpClient agentClient = asyncHttpClient();

    @Value("${agent.provider.serviceName}")
    private String serviceName;

    public DeferredResult<ResponseEntity<String>> invoke(HttpServletRequest request) {
        DeferredResult<ResponseEntity<String>> result = new DeferredResult<>();
        try {
            Endpoint endpoint = iLbStrategy.lbStrategy(iRegistry.find(request.getParameter("interface")));
            RequestBuilder rb = post(new StringBuilder("http://")
                    .append(endpoint.getHost())
                    .append(":")
                    .append(endpoint.getPort())
                    .toString());
            request.getParameterMap().forEach((k, v) -> {
                rb.addFormParam(k, v[0]);
            });
            ListenableFuture<Response> responseFuture = agentClient.executeRequest(rb.build());
            responseFuture.addListener(() -> {
                try {
                    result.setResult(new ResponseEntity(responseFuture.get().getResponseBody(), HttpStatus.OK));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
