package com.stouduo.mesh.rpc.client;


public interface ConsumerRpcClient {
    Object invoke(RpcRequest request) throws Exception;
}
