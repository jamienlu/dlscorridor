package cn.jamie.dlscorridor.core.transform;

import cn.jamie.dlscorridor.core.api.RpcRequest;
import cn.jamie.dlscorridor.core.api.RpcResponse;
import cn.jamie.dlscorridor.core.meta.InstanceMeta;

/**
 * @author jamieLu
 * @create 2024-04-08
 */
public interface RpcTransform {
    RpcResponse transform(RpcRequest rpcRequest, InstanceMeta instanceMeta);
}
