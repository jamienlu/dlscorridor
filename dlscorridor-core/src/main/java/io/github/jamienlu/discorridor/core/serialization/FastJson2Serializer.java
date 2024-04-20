package io.github.jamienlu.discorridor.core.serialization;

import com.alibaba.fastjson2.JSONB;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;

/**
 * @author jamieLu
 * @create 2024-03-16
 */
public class FastJson2Serializer implements SerializationService {
    @Override
    public <T> byte[] serialize(T obj) {
        return JSONB.toBytes(obj, JSONWriter.Feature.WriteClassName,
                JSONWriter.Feature.FieldBased,
                JSONWriter.Feature.ReferenceDetection,
                JSONWriter.Feature.WriteNulls,
                JSONWriter.Feature.NotWriteDefaultValue,
                JSONWriter.Feature.NotWriteHashMapArrayListClassName,
                JSONWriter.Feature.WriteNameAsSymbol);
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        return JSONB.parseObject(data, clazz,
                JSONReader.Feature.UseDefaultConstructorAsPossible,
                JSONReader.Feature.UseNativeObject,
                JSONReader.Feature.IgnoreAutoTypeNotMatch,
                JSONReader.Feature.FieldBased);
    }
}
