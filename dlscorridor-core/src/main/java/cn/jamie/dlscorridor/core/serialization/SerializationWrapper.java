package cn.jamie.dlscorridor.core.serialization;

import lombok.Data;
/**
 * @author jamieLu
 * @create 2024-04-12
 */
@Data
public class SerializationWrapper<T> {
    private T data;
    public static <T> SerializationWrapper<T> wrapper(T data) {
        SerializationWrapper<T> serializationWrapper = new SerializationWrapper();
        serializationWrapper.setData(data);
       return serializationWrapper;
    }
}
