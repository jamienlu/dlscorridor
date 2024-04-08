package cn.jamie.dlscorridor.core.filter;

import cn.jamie.dlscorridor.core.api.RpcContext;
import cn.jamie.dlscorridor.core.api.RpcInvokeHandler;
import cn.jamie.dlscorridor.core.api.RpcRequest;
import cn.jamie.dlscorridor.core.api.RpcResponse;
import com.alibaba.fastjson2.JSON;
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
