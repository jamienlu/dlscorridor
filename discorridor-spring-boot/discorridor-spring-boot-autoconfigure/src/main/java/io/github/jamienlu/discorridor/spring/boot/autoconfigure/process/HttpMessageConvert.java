package io.github.jamienlu.discorridor.spring.boot.autoconfigure.process;

import io.github.jamienlu.discorridor.core.serialization.SerializationService;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author jamieLu
 * @create 2024-04-16
 */
public class HttpMessageConvert extends AbstractHttpMessageConverter {
    private final SerializationService serializationService;

    public HttpMessageConvert(SerializationService serializationService) {
        super(switch (serializationService.getClass().getSimpleName()) {
            case "FastJson2Serializer" -> new MediaType("application","json", StandardCharsets.UTF_8);
            case "ProtobufSerializer" -> new MediaType("application","x-protobuf", StandardCharsets.UTF_8);
            default -> new MediaType("application","json", StandardCharsets.UTF_8);
        });
        this.serializationService = serializationService;
    }

    @Override
    protected boolean supports(Class clazz) {
        return true;
    }

    @Override
    protected Object readInternal(Class clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        byte[] outBytes = IOUtils.toByteArray(inputMessage.getBody());
        return serializationService.deserialize(outBytes, clazz);
    }

    @Override
    protected void writeInternal(Object o, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        outputMessage.getBody().write(serializationService.serialize(o));
    }
}
