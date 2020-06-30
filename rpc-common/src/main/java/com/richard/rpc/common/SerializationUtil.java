package com.richard.rpc.common;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SerializationUtil {

    private static Map<Class<?>, Schema<?>> schemaMap = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    protected static <T> byte[] serialize(T obj) {
        Class<T> clazz = (Class<T>) obj.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        Schema<T> schema = getSchema(clazz);
        try {
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } finally {
            buffer.clear();
        }
    }

    protected static <T> T deserialize(byte[] data, Class<T> clazz) {
        Schema<T> schema = getSchema(clazz);
        T obj = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(data, obj, schema);
        return obj;
    }

    @SuppressWarnings("unchecked")
    private static <T> Schema<T> getSchema(Class<T> clazz) {
        Schema<T> schema = (Schema<T>) schemaMap.get(clazz);
        if (schema == null) {
            schema = RuntimeSchema.createFrom(clazz);
            schemaMap.put(clazz, schema);
        }
        return schema;
    }
}
