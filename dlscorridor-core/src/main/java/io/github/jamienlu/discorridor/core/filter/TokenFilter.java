package io.github.jamienlu.discorridor.core.filter;

import io.github.jamienlu.discorridor.common.api.RpcInvokeHandler;
import io.github.jamienlu.discorridor.common.api.RpcRequest;
import io.github.jamienlu.discorridor.common.api.RpcResponse;
import io.github.jamienlu.discorridor.common.exception.RpcException;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author jamieLu
 * @create 2024-03-25
 */
@Slf4j
public class TokenFilter implements Filter {
    private final long maxSize;
    private final long incurRate;

    private final AtomicLong currentSize;
    private volatile long lastIncurTimestamp;

    public TokenFilter(long maxSize, long incurRate) {
        this.maxSize = maxSize;
        this.incurRate = incurRate;
        this.currentSize = new AtomicLong(maxSize);
        this.lastIncurTimestamp = System.nanoTime();
    }

    /**
     * 根据时间补充令牌
     */
    private synchronized void incur() {
        long now = System.nanoTime();
        // nanoseconds to seconds
        long tokensToAdd = ((now - lastIncurTimestamp) / 1_000_000_000) * incurRate;
        // 时间差小于1s不补令牌
        if (tokensToAdd < 1) {
            return;
        }
        // 添加token许可
        if (currentSize.get() + tokensToAdd > maxSize) {
            currentSize.set(maxSize);
        } else {
            currentSize.addAndGet(tokensToAdd);
        }
        lastIncurTimestamp = now;
    }
    @Override
    public void filter(RpcRequest rpcRequest, RpcResponse rpcResponse, RpcInvokeHandler rpcInvokeHandler, FilterChain filterChain) {
        log.info("start token filter:" + rpcRequest);
        // 自动根据时间补令牌
        incur();
        if (currentSize.decrementAndGet() > 0) {
            log.info("token acquire this invoke:" + currentSize.get());
            filterChain.doFilter(rpcRequest,rpcResponse,rpcInvokeHandler);
            log.info("token end this invoke");
        } else {
            log.error("no use token can not process filter");
            rpcResponse.setStatus(false);
            rpcResponse.setEx(new RpcException(RpcException.NO_TOKEN));
        }
    }
}
