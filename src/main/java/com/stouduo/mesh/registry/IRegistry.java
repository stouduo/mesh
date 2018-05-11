package com.stouduo.mesh.registry;

import com.stouduo.mesh.util.Endpoint;

import java.util.List;

public interface IRegistry {

    // 注册服务
    void register(String serviceName, Endpoint endpoint) throws Exception;

    void register() throws Exception;

    List<Endpoint> find(String serviceName) throws Exception;

    void serverDown(Endpoint endpoint) throws Exception;

    void serverDown() throws Exception;

}
