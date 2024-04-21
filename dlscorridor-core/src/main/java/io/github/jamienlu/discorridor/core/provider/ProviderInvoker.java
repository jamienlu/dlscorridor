package io.github.jamienlu.discorridor.core.provider;

import io.github.jamienlu.discorridor.core.api.RpcContext;
import io.github.jamienlu.discorridor.common.api.RpcInvokeHandler;
import io.github.jamienlu.discorridor.common.api.RpcRequest;
import io.github.jamienlu.discorridor.common.api.RpcResponse;
import io.github.jamienlu.discorridor.common.exception.RpcException;
import io.github.jamienlu.discorridor.core.meta.ProviderMeta;
import io.github.jamienlu.discorridor.common.meta.ServiceMeta;
import io.github.jamienlu.discorridor.core.util.SlidingTimeWindow;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author jamieLu
 * @create 2024-03-20
 */
@Slf4j
public class ProviderInvoker implements RpcInvokeHandler {
    private final ProviderStorage providerStorage;
    private final ServiceMeta serverMeta;

    final Map<String, SlidingTimeWindow> windows = new ConcurrentHashMap<>();

    public ProviderInvoker(ProviderStorage providerStorage, ServiceMeta serverMeta) {
        this.providerStorage = providerStorage;
        this.serverMeta = serverMeta;
    }

    /**
     * 从服务提供者嗲用服务方法
     *
     * @param rpcRequest 调用对象
     * @return RpcResponse 调用结果
     */
    @Override
    public RpcResponse doInvoke(RpcRequest rpcRequest) {
        log.debug(" ===> ProviderInvoker.invoke(request:{})", rpcRequest);
        RpcResponse rpcResponse = RpcResponse.builder().build();
        // 设置线程上下文环境参数
        if(!rpcRequest.getParameters().isEmpty()) {
            log.debug("providerInvoker invoker add thread context!");
            rpcRequest.getParameters().forEach(RpcContext::setContextParameter);
        }
        ProviderMeta providerMeta = providerStorage.findProviderMeta(rpcRequest.getService(),rpcRequest.getMethodSign());
        if (providerMeta != null) {
            SlidingTimeWindow window = windows.computeIfAbsent(rpcRequest.getService(), k -> new SlidingTimeWindow());
            if (window.calcSum() >= Integer.parseInt(serverMeta.searchMeta("tc"))) {
                throw new RpcException(RpcException.SERVER_OVERLOAD);
            } else {
                window.record(System.currentTimeMillis());
                log.debug("service {} in window with {}", rpcRequest.getService(), window.getSum());
            }
            Object data = null;
            Method method = providerMeta.getMethod();
            // json 序列化还原  数组和集合类型数据处理
            Object[] realArgs = new Object[method.getParameterTypes().length];
            if (rpcRequest.getArgs() != null) {
                for (int i = 0; i < realArgs.length; i++) {
                    realArgs[i] = JSON.to(method.getParameterTypes()[i], rpcRequest.getArgs()[i]);
                }
            }
            try {
                data = method.invoke(providerMeta.getServiceImpl(), realArgs);
                rpcResponse.setData(data);
                rpcResponse.setStatus(true);
            } catch (InvocationTargetException e) {
                log.info(method.getDeclaringClass().getName() + "##" + method.getName() + "##" + Arrays.toString(realArgs));
                log.error("invoke error:", e);
                rpcResponse.setEx(new RpcException(e.getTargetException().getMessage()));
            } catch (IllegalAccessException e) {
                rpcResponse.setEx(new RpcException(e.getCause(),e.getMessage()));
            } finally {
                log.debug("providerInvoker contextParameters param:{}", RpcContext.contextParameters.get());
                RpcContext.contextParameters.get().clear();
            }
        } else {
            rpcResponse.setEx(new RpcException(RpcException.NO_SUCH_METHOD_EX));
        }
        return rpcResponse;
    }
}
