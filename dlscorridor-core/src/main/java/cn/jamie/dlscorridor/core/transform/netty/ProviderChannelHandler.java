package cn.jamie.dlscorridor.core.transform.netty;

import cn.jamie.dlscorridor.core.api.RpcRequest;
import cn.jamie.dlscorridor.core.api.RpcResponse;
import cn.jamie.dlscorridor.core.provider.ProviderInvoker;
import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jamieLu
 * @create 2024-04-10
 */
@Slf4j
public class ProviderChannelHandler extends ChannelDuplexHandler implements ChannelInboundHandler {
    private final ProviderInvoker providerInvoker;

    public ProviderChannelHandler(ProviderInvoker providerInvoker) {
        this.providerInvoker = providerInvoker;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.debug("revceive client data:" + JSON.toJSONString(msg));
        // 响应客户端
        RpcRequest rpcRequest = JSON.to(RpcRequest.class, msg);
        RpcResponse rpcResponse = providerInvoker.doInvoke(rpcRequest);
        // 回写请求
        ctx.writeAndFlush(rpcResponse);
    }
}
