package com.stouduo.mesh.server.netty.util;

import com.stouduo.mesh.dubbo.model.RpcResponse;
import com.stouduo.mesh.rpc.client.RpcRequest;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.runtime.RuntimeSchema;

import java.util.HashMap;
import java.util.Map;

public class ProtoSerializeUtil {
    private static Map<String, RuntimeSchema> schemas = new HashMap<>();

    static {
        schemas.put(RpcRequest.class.getName(), RuntimeSchema.createFrom(RpcRequest.class));
        schemas.put(RpcResponse.class.getName(), RuntimeSchema.createFrom(RpcResponse.class));
    }

    public static <T> byte[] serialize(T target) {
        return ProtostuffIOUtil.toByteArray(target, schemas.get(target.getClass().getName()), LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
    }

    public static <T> T deserialize(byte[] array, Class<T> clazz) {
        RuntimeSchema schema = schemas.get(clazz.getName());
        T target = (T) schema.newMessage();
        ProtostuffIOUtil.mergeFrom(array, target, schema);
        return target;
    }
}
