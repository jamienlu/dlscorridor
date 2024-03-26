package cn.jamie.dlscorridor.core.filter;

import cn.jamie.dlscorridor.core.api.RpcRequest;
import cn.jamie.dlscorridor.core.api.RpcResponse;

import java.util.function.Function;

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
     * @param rpcInvoke rpc函数
     */
    void doFilter(RpcRequest rpcRequest, RpcResponse rpcResponse, Function<RpcRequest,RpcResponse> rpcInvoke);
}
