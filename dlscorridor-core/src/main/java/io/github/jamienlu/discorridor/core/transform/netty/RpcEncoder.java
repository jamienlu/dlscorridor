package io.github.jamienlu.discorridor.core.transform.netty;

import io.github.jamienlu.discorridor.serialization.api.SerializationService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author jamieLu
 * @create 2024-04-10
 */
public class RpcEncoder extends MessageToByteEncoder<Object> {
    private final SerializationService serializationService;

    public RpcEncoder(SerializationService serializationService) {
        super();
        this.serializationService = serializationService;
    }
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) {
        byte[] bytes = serializationService.serialize(o);
        byteBuf.writeBytes(bytes);
    }
}
