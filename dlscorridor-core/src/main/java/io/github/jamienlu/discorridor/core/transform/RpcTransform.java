package io.github.jamienlu.discorridor.core.transform;

import io.github.jamienlu.discorridor.core.api.RpcRequest;
import io.github.jamienlu.discorridor.core.api.RpcResponse;
import io.github.jamienlu.discorridor.core.meta.InstanceMeta;

/**
 * @author jamieLu
 * @create 2024-04-08
 */
public interface RpcTransform {
    RpcResponse transform(RpcRequest rpcRequest, InstanceMeta instanceMeta);
}
