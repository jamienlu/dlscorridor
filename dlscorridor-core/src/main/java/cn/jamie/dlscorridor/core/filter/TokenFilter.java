package cn.jamie.dlscorridor.core.filter;

import cn.jamie.dlscorridor.core.api.RpcRequest;
import cn.jamie.dlscorridor.core.api.RpcResponse;
import cn.jamie.dlscorridor.core.util.RpcUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

/**
 * @author jamieLu
 * @create 2024-03-25
 */
@Slf4j
public class TokenFilter implements Filter {
    private final long maxSize;
    private final long incurRate;

    private AtomicLong currentSize;
    private long lastIncurTimestamp;

    public TokenFilter(long maxSize, long incurRate) {
        this.maxSize = maxSize;
        this.incurRate = incurRate;
        this.currentSize = new AtomicLong(maxSize);
        this.lastIncurTimestamp = System.nanoTime();
    }
    private synchronized void incur() {
        long now = System.nanoTime();
        // nanoseconds to seconds
        long tokensToAdd = ((now - lastIncurTimestamp) / 1_000_000_000) * incurRate;
        // 添加token许可
        if (currentSize.get() + tokensToAdd > maxSize) {
            currentSize.set(maxSize);
        } else {
            currentSize.addAndGet(tokensToAdd);
        }
        lastIncurTimestamp = now;
    }
    @Override
    public void filter(RpcRequest rpcRequest, RpcResponse rpcResponse, Function<RpcRequest, RpcResponse> invoke, FilterChain filterChain) {
        log.info("start token filter:" + rpcRequest);
        // 自动根据时间补令牌
        incur();
        if (currentSize.decrementAndGet() > 0) {
            log.info("token acquire this invoke");
            filterChain.doFilter(rpcRequest,rpcResponse,invoke);
            log.info("token end this invoke");
        } else {
            log.error("no use token skip this filter");
            rpcResponse.setStatus(false);
            rpcResponse.setEx(new RuntimeException("too fast! no token use"));
        }
    }
}
