package com.stouduo.mesh.rpc.client.impl;

import com.stouduo.mesh.rpc.client.RpcClient;
import com.stouduo.mesh.rpc.client.RpcRequest;
import okhttp3.ConnectionPool;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.util.concurrent.TimeUnit;

public class AgentRpcClient implements RpcClient {
    private OkHttpClient okHttpClient;

    public AgentRpcClient() {
        ConnectionPool pool = new ConnectionPool(100, 5L, TimeUnit.MINUTES);
        this.okHttpClient = new OkHttpClient.Builder()
                .connectionPool(pool)
                .connectTimeout(60, TimeUnit.SECONDS)       //设置连接超时
                .readTimeout(60, TimeUnit.SECONDS)          //不考虑超时
                .writeTimeout(60, TimeUnit.SECONDS)          //不考虑超时
                .retryOnConnectionFailure(true)
                .build();
    }

    @Override
    public Object invoke(RpcRequest request) {
        try {
            FormBody.Builder formBody = new FormBody.Builder();
            request.getParameters().forEach((k, v) -> {
                formBody.add(k, (String) v);
            });
            return okHttpClient.newCall(new Request.Builder()
                    .url(request.getRequsetUrl())
                    .post(formBody.build())
                    .build()).execute().body().bytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
