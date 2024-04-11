package cn.jamie.dlscorridor.core.transform.netty;

import cn.jamie.dlscorridor.core.api.RpcRequest;
import cn.jamie.dlscorridor.core.serialization.SerializationService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * @author jamieLu
 * @create 2024-04-09
 */
public class RpcRequestDecoder extends MessageToMessageDecoder<ByteBuf> {
    private final Class<RpcRequest> clazz = RpcRequest.class;
    private final SerializationService serializationService;

    public RpcRequestDecoder(SerializationService serializationService) {
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
