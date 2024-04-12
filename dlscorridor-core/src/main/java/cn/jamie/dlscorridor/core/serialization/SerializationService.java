package cn.jamie.dlscorridor.core.serialization;

import java.io.IOException;

/**
 * 序列化接口
 *
 * @author jamieLu
 * @create 2024-03-15
 */
public interface SerializationService {
    <T> byte[] serialize(T obj);
    <T> T deserialize(byte[] data, Class<T> clazz);
}
