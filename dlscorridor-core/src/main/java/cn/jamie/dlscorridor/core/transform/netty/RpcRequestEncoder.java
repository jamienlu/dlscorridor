package cn.jamie.dlscorridor.core.transform.netty;

import cn.jamie.dlscorridor.core.api.RpcRequest;
import cn.jamie.dlscorridor.core.serialization.SerializationService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author jamieLu
 * @create 2024-04-10
 */
public class RpcRequestEncoder extends MessageToByteEncoder<RpcRequest> {
    private final SerializationService serializationService;

    public RpcRequestEncoder(SerializationService serializationService) {
        super();
        this.serializationService = serializationService;
    }
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcRequest o, ByteBuf byteBuf) throws Exception {
        byte[] bytes = serializationService.serialize(o);
        byteBuf.writeBytes(bytes);
    }
}
