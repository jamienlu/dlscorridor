package cn.jamie.dlscorridor.core.serialization;

import com.alibaba.fastjson2.JSONB;

import java.io.IOException;

/**
 * @author jamieLu
 * @create 2024-03-16
 */
public class FastJson2Serializer implements SerializationService {
    @Override
    public <T> byte[] serialize(T obj) {
        return JSONB.toBytes(obj);
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        return JSONB.parseObject(data, clazz);
    }
}
