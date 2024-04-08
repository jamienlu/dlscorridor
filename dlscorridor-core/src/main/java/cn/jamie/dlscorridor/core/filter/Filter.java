package cn.jamie.dlscorridor.core.filter;

import cn.jamie.dlscorridor.core.api.RpcInvokeHandler;
import cn.jamie.dlscorridor.core.api.RpcRequest;
import cn.jamie.dlscorridor.core.api.RpcResponse;
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
     * @param rpcInvokeHandler rpc函数
     * @param filterChain filter链路
     */
    void filter(RpcRequest rpcRequest, RpcResponse rpcResponse, RpcInvokeHandler rpcInvokeHandler, FilterChain filterChain);


}
