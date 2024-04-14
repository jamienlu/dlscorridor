package cn.jamie.dlscorridor.core.transform.netty;

import cn.jamie.dlscorridor.core.serialization.SerializationService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * @author jamieLu
 * @create 2024-04-10
 */
public class RpcDecoder extends MessageToMessageDecoder<ByteBuf> {
    private final Class<?> clazz;
    private final SerializationService serializationService;

    public RpcDecoder(Class<?> clazz, SerializationService serializationService) {
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