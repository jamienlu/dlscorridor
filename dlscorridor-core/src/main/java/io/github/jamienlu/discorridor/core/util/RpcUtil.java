package io.github.jamienlu.discorridor.core.util;

import io.github.jamienlu.discorridor.core.api.RpcResponse;

/**
 * @author jamieLu
 * @create 2024-03-25
 */
public class RpcUtil {
    public static void cloneRpcResponse(RpcResponse clone, RpcResponse target) {
        if (clone != null && target != null) {
            clone.setStatus(target.isStatus());
            clone.setEx(target.getEx());
            clone.setData(target.getData());
        }
    }
}
