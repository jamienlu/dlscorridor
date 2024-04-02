package cn.jamie.dlscorridor.core.provider;

import cn.jamie.dlscorridor.core.api.RpcRequest;
import cn.jamie.dlscorridor.core.api.RpcResponse;
import cn.jamie.dlscorridor.core.exception.RpcException;
import cn.jamie.dlscorridor.core.meta.ProviderMeta;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author jamieLu
 * @create 2024-03-20
 */
@Slf4j
public class ProviderInvoker {
    private ProviderStorage providerStorage;

    public ProviderInvoker(ProviderStorage providerStorage) {
        this.providerStorage = providerStorage;
    }

    /**
     * 从服务提供者嗲用服务方法
     *
     * @param rpcRequest 调用对象
     * @return RpcResponse 调用结果
     */
    public RpcResponse<Object> invoke(RpcRequest rpcRequest) {
        RpcResponse<Object> rpcResponse = RpcResponse.builder().build();
        ProviderMeta providerMeta = providerStorage.findProviderMeta(rpcRequest.getService(),rpcRequest.getMethodSign());
        if (providerMeta != null) {
            Object data = null;
            Method method = providerMeta.getMethod();
            // json 序列化还原  数组和集合类型数据处理
            Object[] realArgs = new Object[method.getParameterTypes().length];
            for (int i = 0; i < realArgs.length; i++) {
                realArgs[i] = JSON.to(method.getParameterTypes()[i],rpcRequest.getArgs()[i]);
            }
            try {
                data = method.invoke(providerMeta.getServiceImpl(), realArgs);
                rpcResponse.setData(data);
                rpcResponse.setStatus(true);
            } catch (InvocationTargetException | IllegalAccessException e) {
                log.info(method.getDeclaringClass().getName());
                log.error("invoke error:", e);
                rpcResponse.setStatus(false);
                rpcResponse.setEx(new RpcException(e.getCause(),e.getMessage()));
            }
        } else {
            rpcResponse.setEx(new RpcException(RpcException.NO_SUCH_METHOD_EX));
        }

        return rpcResponse;
    }
}
