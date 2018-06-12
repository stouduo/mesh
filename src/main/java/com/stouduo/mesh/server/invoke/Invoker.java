package com.stouduo.mesh.server.invoke;

import com.stouduo.mesh.dubbo.model.RpcDTO;

public interface Invoker {
    void invoke(RpcDTO data);
}
