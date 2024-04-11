package cn.jamie.dlscorridor.core.transform.netty;

import cn.jamie.dlscorridor.core.api.RpcResponse;
import cn.jamie.dlscorridor.core.serialization.SerializationService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author jamieLu
 * @create 2024-04-10
 */
public class RpcResponseEncoder extends MessageToByteEncoder<RpcResponse> {
    private final SerializationService serializationService;

    public RpcResponseEncoder(SerializationService serializationService) {
        super();
        this.serializationService = serializationService;
    }
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcResponse o, ByteBuf byteBuf) throws Exception {
        byte[] bytes = serializationService.serialize(o);
        byteBuf.writeBytes(bytes);
    }
}
