package cn.jamie.dlscorridor.core.serialization;

import java.io.IOException;

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
