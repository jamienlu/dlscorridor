package cn.jamie.dlscorridor.core.filter;

import cn.jamie.dlscorridor.core.api.RpcRequest;
import cn.jamie.dlscorridor.core.api.RpcResponse;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author jamieLu
 * @create 2024-03-17
 */
public interface Filter {
    /**
     * 过滤方法
     *
     * @param rpcRequest 入参
     * @param rpcResponse 出参
     * @param rpcInvoke rpc函数
     * @param filterChain filter链路
     */
    void filter(RpcRequest rpcRequest, RpcResponse rpcResponse, Function<RpcRequest,RpcResponse> rpcInvoke,FilterChain filterChain);


}
