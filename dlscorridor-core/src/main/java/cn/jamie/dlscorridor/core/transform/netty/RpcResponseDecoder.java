package cn.jamie.dlscorridor.core.transform.netty;

import cn.jamie.dlscorridor.core.api.RpcResponse;
import cn.jamie.dlscorridor.core.serialization.SerializationService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * @author jamieLu
 * @create 2024-04-10
 */
public class RpcResponseDecoder extends MessageToMessageDecoder<ByteBuf> {
    private final Class<RpcResponse> clazz = RpcResponse.class;
    private final SerializationService serializationService;

    public RpcResponseDecoder(SerializationService serializationService) {
        super();
        this.serializationService = serializationService;
    }
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        Object obj = serializationService.deserialize(bytes, clazz);
        list.add(obj);
    }
}
