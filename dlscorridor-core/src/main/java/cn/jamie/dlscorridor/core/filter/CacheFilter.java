package cn.jamie.dlscorridor.core.filter;

import cn.jamie.dlscorridor.core.api.RpcRequest;
import cn.jamie.dlscorridor.core.api.RpcResponse;
import cn.jamie.dlscorridor.core.util.RpcUtil;
import com.alibaba.fastjson2.JSON;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.annotation.Order;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * RPC相同请求不进行RPC从Cache里获取
 *
 * @author jamieLu
 * @create 2024-03-25
 */
@Slf4j
@Order(-1)
public class CacheFilter implements Filter {
    private final LoadingCache<String, RpcResponse> guravaCache;

    public CacheFilter(Integer size, Integer timeout) {
        guravaCache = CacheBuilder.newBuilder()
                .maximumSize(size) // 最多存储50个元素
                .expireAfterAccess(timeout, TimeUnit.MINUTES) // 访问后5分钟过期
                .build(new CacheLoader<>() {
                    @Override
                    public RpcResponse load(String key) {
                        // 缓存不命中自定义如何加载缓存
                        throw new UnsupportedOperationException("auto Loading is not supported.");
                    }
                });
    }
    @Override
    public void filter(RpcRequest rpcRequest, RpcResponse rpcResponse, Function<RpcRequest,RpcResponse> rpcInvoke, FilterChain filterChain) {
        log.info("start cache filter:" + rpcRequest);
        RpcResponse result = guravaCache.getIfPresent(rpcRequest.toString());
        if (result == null) {
            log.info("not fount cache key:" + rpcRequest);
            filterChain.doFilter(rpcRequest,rpcResponse,rpcInvoke);
            // 返回的结果无异常才缓存
            if (rpcResponse.isStatus()) {
                log.info("save cache value:" + JSON.toJSONString(rpcResponse));
                guravaCache.put(rpcRequest.toString(), rpcResponse);
            }
        }
        RpcUtil.cloneRpcResponse(rpcResponse, result);
        log.info("end cache filter:" + rpcRequest);
    }
}
