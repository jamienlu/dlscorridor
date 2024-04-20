package io.github.jamienlu.discorridor.core.filter;

import io.github.jamienlu.discorridor.core.api.RpcContext;
import io.github.jamienlu.discorridor.core.api.RpcInvokeHandler;
import io.github.jamienlu.discorridor.core.api.RpcRequest;
import io.github.jamienlu.discorridor.core.api.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author jamieLu
 * @create 2024-04-08
 */
@Slf4j
public class RpcContextFilter implements Filter {
    @Override
    public void filter(RpcRequest rpcRequest, RpcResponse rpcResponse, RpcInvokeHandler rpcInvokeHandler, FilterChain filterChain) {
        Map<String,String> threadContext = RpcContext.contextParameters.get();
        if (!threadContext.isEmpty()) {
            rpcRequest.setParameters(threadContext);
        }
        filterChain.doFilter(rpcRequest,rpcResponse,rpcInvokeHandler);
        // 调用结束清理上下文环境 防止内存泄漏和污染
        RpcContext.contextParameters.remove();
    }
}
