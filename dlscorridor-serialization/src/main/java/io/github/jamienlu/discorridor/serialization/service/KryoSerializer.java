package io.github.jamienlu.discorridor.serialization.service;

import io.github.jamienlu.discorridor.serialization.api.SerializationService;

/**
 * @author jamieLu
 * @create 2024-03-16
 */
public class KryoSerializer implements SerializationService {
    @Override
    public <T> byte[] serialize(T obj) {
        return new byte[0];
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        return null;
    }
}
