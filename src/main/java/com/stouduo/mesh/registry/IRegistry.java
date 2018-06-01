package com.stouduo.mesh.registry;


import com.stouduo.mesh.dubbo.model.RpcDTO.Endpoint;

import java.util.List;

public interface IRegistry {

    // 注册服务
    void register() throws Exception;

    List<Endpoint> find(String serviceName) throws Exception;

    void serverDown(Endpoint endpoint) throws Exception;

}
