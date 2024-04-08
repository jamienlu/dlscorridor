package cn.jamie.dlscorridor.core.consumer;

import cn.jamie.dlscorridor.core.api.RpcContext;
import cn.jamie.dlscorridor.core.api.RpcInvokeHandler;
import cn.jamie.dlscorridor.core.api.RpcRequest;
import cn.jamie.dlscorridor.core.api.RpcResponse;
import cn.jamie.dlscorridor.core.meta.InstanceMeta;
import cn.jamie.dlscorridor.core.util.RpcMethodUtil;
import cn.jamie.dlscorridor.core.util.RpcReflectUtil;

import cn.jamie.dlscorridor.core.util.TypeUtil;
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
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // Object父类方法禁止远程调用
        if (RpcMethodUtil.notPermissionMethod(method.getName())) {
            return null;
        }
        // 组装远程调用参数
        RpcRequest rpcRequest = RpcRequest.builder()
            .service(service.getCanonicalName())
            .methodSign(RpcReflectUtil.analysisMethodSign(method))
            .args(args).build();
        RpcResponse rpcResponse = RpcResponse.builder().status(false).data(null).build();
        rpcContext.getFilterChain().doFilter(rpcRequest, rpcResponse, rpcHandler);
        if (rpcResponse.isStatus()) {
            return TypeUtil.castMethodResult(method, rpcResponse.getData());
        } else {
            throw rpcResponse.getEx();
        }
    }
}
