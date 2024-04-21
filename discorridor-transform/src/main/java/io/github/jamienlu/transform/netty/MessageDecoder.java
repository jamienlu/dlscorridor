package io.github.jamienlu.transform.netty;

import io.github.jamienlu.discorridor.serialization.api.SerializationService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * @author jamieLu
 * @create 2024-04-10
 */
public class MessageDecoder extends MessageToMessageDecoder<ByteBuf> {
    private final Class<?> clazz;
    private final SerializationService serializationService;

    public MessageDecoder(Class<?> clazz, SerializationService serializationService) {
        this.clazz = clazz;
        this.serializationService = serializationService;
    }
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        Object obj = serializationService.deserialize(bytes, clazz);
        list.add(obj);
    }
}
