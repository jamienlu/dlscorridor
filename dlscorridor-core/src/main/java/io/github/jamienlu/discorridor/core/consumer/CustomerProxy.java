package io.github.jamienlu.discorridor.core.consumer;

import io.github.jamienlu.discorridor.core.api.RpcContext;
import io.github.jamienlu.discorridor.common.api.RpcInvokeHandler;
import io.github.jamienlu.discorridor.common.api.RpcRequest;
import io.github.jamienlu.discorridor.common.api.RpcResponse;
import io.github.jamienlu.discorridor.common.meta.InstanceMeta;
import io.github.jamienlu.discorridor.core.util.RpcMethodUtil;
import io.github.jamienlu.discorridor.common.util.ReflectUtil;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
/**
 * 消费这动态代理调用服务提供者
 */
@Slf4j
public class CustomerProxy implements InvocationHandler {
    private final Class<?> service;
    private final RpcContext rpcContext;
    private final RpcInvokeHandler rpcHandler;

    public CustomerProxy(Class<?> service, RpcContext rpcContext, List<InstanceMeta> instanceMetas) {
        this.service = service;
        this.rpcContext = rpcContext;
        this.rpcHandler = new StubInvokerHandler(rpcContext, instanceMetas);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        // Object父类方法禁止远程调用
        if (RpcMethodUtil.notPermissionMethod(method.getName())) {
            return null;
        }
        // 组装远程调用参数
        RpcRequest rpcRequest = RpcRequest.builder()
            .service(service.getCanonicalName())
            .methodSign(ReflectUtil.analysisMethodSign(method))
            .args(args).build();
        RpcResponse rpcResponse = RpcResponse.builder().status(false).data(null).build();
        rpcContext.getFilterChain().doFilter(rpcRequest, rpcResponse, rpcHandler);
        if (rpcResponse.isStatus()) {
            return rpcResponse.getData() /*TypeUtil.castMethodResult(method, rpcResponse.getData())*/;
        } else {
            throw rpcResponse.getEx();
        }
    }
}
