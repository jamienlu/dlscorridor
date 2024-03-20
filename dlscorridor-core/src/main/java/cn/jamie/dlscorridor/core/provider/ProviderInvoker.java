package cn.jamie.dlscorridor.core.provider;

import cn.jamie.dlscorridor.core.api.RpcRequest;
import cn.jamie.dlscorridor.core.api.RpcResponse;
import cn.jamie.dlscorridor.core.meta.ProviderMeta;
import com.alibaba.fastjson2.JSON;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author jamieLu
 * @create 2024-03-20
 */
public class ProviderInvoker {
    private ProviderBootstrap providerBootstrap;

    public ProviderInvoker(ProviderBootstrap providerBootstrap) {
        this.providerBootstrap = providerBootstrap;
    }

    /**
     * 从服务提供者嗲用服务方法
     *
     * @param rpcRequest 调用对象
     * @return RpcResponse 调用结果
     */
    public RpcResponse<Object> invoke(RpcRequest rpcRequest) {
        RpcResponse<Object> rpcResponse = RpcResponse.builder().build();
        Map<String,Map<String, ProviderMeta>> skeltonMap = providerBootstrap.getSkeltonMap();
        Map<String, ProviderMeta> providerMetaMap = skeltonMap.get(rpcRequest.getService());
        if (providerMetaMap != null) {
            ProviderMeta providerMeta = providerMetaMap.get(rpcRequest.getMethodSign());
            if (providerMeta != null) {
                Object data = null;
                Method method = providerMeta.getMethod();
                // json 序列化还原  数组和集合类型数据处理
                Object[] realArgs = new Object[method.getParameterTypes().length];
                for (int i = 0; i < realArgs.length; i++) {
                    realArgs[i] = JSON.to( method.getParameterTypes()[i],rpcRequest.getArgs()[i]);
                }
                try {
                    data = method.invoke(providerMeta.getServiceImpl(), realArgs);
                    rpcResponse.setData(data);
                    rpcResponse.setStatus(true);
                } catch (Exception e) {
                    rpcResponse.setStatus(false);
                    rpcResponse.setEx(e);
                }
            }
        }
        return rpcResponse;
    }
}
