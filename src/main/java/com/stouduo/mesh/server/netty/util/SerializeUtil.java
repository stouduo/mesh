package com.stouduo.mesh.server.netty.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.io.Serializable;

public class SerializeUtil {

    public static byte[] serialize(Object obj) {
        try (SerializeWriter out = new SerializeWriter()) {
            JSONSerializer serializer = new JSONSerializer(out);
            serializer.config(SerializerFeature.WriteEnumUsingToString, true);
            serializer.write(obj);
            return out.toBytes("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T extends Serializable> T deserialize(byte[] obj,
                                                         Class<T> clazz) {
        return (T) JSON.parseObject(obj, clazz);
    }
}
