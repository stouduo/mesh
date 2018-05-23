package com.stouduo.mesh.server.netty.util;

import com.stouduo.mesh.dubbo.model.RpcResponse;
import com.stouduo.mesh.rpc.RpcRequest;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SerializeUtil {
    private static Map<Class<?>, Schema> schemas = new HashMap<>();

    static {
        schemas.put(RpcResponse.class, RuntimeSchema.createFrom(RpcResponse.class));
        schemas.put(RpcRequest.class, RuntimeSchema.createFrom(RpcRequest.class));
    }

    public static byte[] serialize(Object obj) {
        return ProtostuffIOUtil.toByteArray(obj, schemas.get(obj.getClass()), LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE * 2));
    }

    public static <T extends Serializable> T deserialize(byte[] obj,
                                                         Class<T> clazz) {
        T ret = null;
        try {
            Schema schema = schemas.get(clazz);
            ret = (T) schema.newMessage();
            ProtostuffIOUtil.mergeFrom(obj, ret, schema);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
}
