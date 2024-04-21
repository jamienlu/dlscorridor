package io.github.jamienlu.transform.netty;

import io.github.jamienlu.discorridor.common.api.RpcResponse;
import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

/**
 * @author jamieLu
 * @create 2024-04-09
 */
@Slf4j
public class ConsumerChannelHandler extends ChannelDuplexHandler implements ChannelInboundHandler {
    private RpcResponse rpcResponse;
    private final CountDownLatch countDownLatch = new CountDownLatch(1);
    public RpcResponse receiveResponse() {
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return rpcResponse;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        rpcResponse = (RpcResponse) msg;
        countDownLatch.countDown();
        log.debug("revceive server data:" + JSON.toJSONString(rpcResponse));
        // 请求响应模式
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("customer netty read error", cause);
        ctx.close();
    }
}
