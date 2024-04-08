package cn.jamie.dlscorridor.core.api;

/**
 * @author jamieLu
 * @create 2024-04-08
 */
public interface RpcInvokeHandler {
    RpcResponse doInvoke(RpcRequest rpcRequest);
}
