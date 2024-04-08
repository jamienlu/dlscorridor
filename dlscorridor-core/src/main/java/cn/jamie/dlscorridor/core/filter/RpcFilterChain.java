package cn.jamie.dlscorridor.core.filter;

import cn.jamie.dlscorridor.core.api.RpcInvokeHandler;
import cn.jamie.dlscorridor.core.api.RpcRequest;
import cn.jamie.dlscorridor.core.api.RpcResponse;
import cn.jamie.dlscorridor.core.util.RpcUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;


/**
 * @author jamieLu
 * @create 2024-03-25
 */
@Slf4j
public class RpcFilterChain implements FilterChain {
    private final List<Filter> filters = new ArrayList<>();
    private static final ThreadLocal<Integer> currentPosition = ThreadLocal.withInitial(() -> 0);
    @Override
    public FilterChain addFilter(Filter filter) {
        filters.add(filter);
        return this;
    }

    @Override
    public void doFilter(RpcRequest rpcRequest, RpcResponse rpcResponse, RpcInvokeHandler rpcInvokeHandler) {
        int pos  = currentPosition.get();
        if (pos < filters.size()) {
            // 调用下一个Filter
            currentPosition.set(pos + 1);
            filters.get(pos).filter(rpcRequest,rpcResponse,rpcInvokeHandler,this);
        } else {
            // 调用rpc函数
            RpcResponse result = rpcInvokeHandler.doInvoke(rpcRequest);
            RpcUtil.cloneRpcResponse(rpcResponse, result);
            currentPosition.set(0);
        }
    }
}
