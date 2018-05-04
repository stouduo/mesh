package com.stouduo.mesh.rpc.client;


public interface RpcClient {
    Object invoke(RpcRequest request) throws Exception;
}
