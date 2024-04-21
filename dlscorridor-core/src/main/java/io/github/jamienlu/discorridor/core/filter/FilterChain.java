package io.github.jamienlu.discorridor.core.filter;

import io.github.jamienlu.discorridor.common.api.RpcInvokeHandler;
import io.github.jamienlu.discorridor.common.api.RpcRequest;
import io.github.jamienlu.discorridor.common.api.RpcResponse;

/**
 * @author jamieLu
 * @create 2024-03-25
 */
public interface FilterChain {
    FilterChain addFilter(Filter filter);

    /**
     * 过滤链  调用filter方法 调用完后执行rpcInvoke
     *
     * @param rpcRequest 请求
     * @param rpcResponse 响应
     * @param rpcInvokeHandler rpc函数处理器
     */
    void doFilter(RpcRequest rpcRequest, RpcResponse rpcResponse, RpcInvokeHandler rpcInvokeHandler);
}
