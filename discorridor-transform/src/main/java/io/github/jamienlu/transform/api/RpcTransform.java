package io.github.jamienlu.transform.api;

import io.github.jamienlu.discorridor.common.api.RpcRequest;
import io.github.jamienlu.discorridor.common.api.RpcResponse;
import io.github.jamienlu.discorridor.common.meta.InstanceMeta;

/**
 * @author jamieLu
 * @create 2024-04-08
 */
public interface RpcTransform {
    RpcResponse transform(RpcRequest rpcRequest, InstanceMeta instanceMeta);
}
