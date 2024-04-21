package io.github.jamienlu.discorridor.common.api;
/**
 * @author jamieLu
 * @create 2024-04-08
 */
public interface RpcInvokeHandler {
    RpcResponse doInvoke(RpcRequest rpcRequest);
}
