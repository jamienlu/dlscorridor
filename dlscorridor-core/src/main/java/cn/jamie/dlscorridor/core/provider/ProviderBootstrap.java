package cn.jamie.dlscorridor.core.provider;

import cn.jamie.dlscorridor.core.annotation.JMProvider;
import cn.jamie.dlscorridor.core.annotation.RpcService;
import cn.jamie.dlscorridor.core.api.RegistryCenter;
import cn.jamie.dlscorridor.core.api.RpcRequest;
import cn.jamie.dlscorridor.core.api.RpcResponse;
import cn.jamie.dlscorridor.core.meta.ProviderMeta;
import cn.jamie.dlscorridor.core.util.RpcMethodUtil;
import cn.jamie.dlscorridor.core.util.RpcReflectUtil;
import com.alibaba.fastjson2.JSON;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
/**
 * 服务提供者封装类接口和其实现类对象
 *
 * @author jamieLu
 * @create 2024-03-12
 */
@Data
public class ProviderBootstrap implements ApplicationContextAware {
    ApplicationContext applicationContext;
    private Map<String,Map<String, ProviderMeta>> skeltonMap = new HashMap<>();
    @Value("${server.port}")
    private String port;
    private String instance;

    @PostConstruct
    /**
     * 映射接口和实现类
     */
    public void initProviders() {
        // 查找服务提供类
        Map<String,Object> providerBeanMap = applicationContext.getBeansWithAnnotation(JMProvider.class);
        // 注入映射的接口和服务提供方法
        providerBeanMap.values()
            // 获取提供服务的接口
            .forEach(providerBean -> RpcReflectUtil.findAnnotationInterfaces(providerBean.getClass(), RpcService.class)
            // 映射实现类和服务方法
            .forEach(intefaceClass -> {
                skeltonMap.putIfAbsent(intefaceClass.getCanonicalName(), new HashMap<>());
                Map<String, ProviderMeta> skeltonBeanMap = skeltonMap.get(intefaceClass.getCanonicalName());
            Arrays.stream(providerBean.getClass().getDeclaredMethods())
                    .filter(method -> !RpcMethodUtil.notPermissionMethod(method.getName()))
                    .forEach(method -> {
                        String methodSign = RpcReflectUtil.analysisMethodSign(method);
                        skeltonBeanMap.put(methodSign, ProviderMeta.builder().methodSign(methodSign).method(method).serviceImpl(providerBean).build());
                    });
            }));

    }
    @SneakyThrows
    public void startRegistryCenter() {
        String ip = InetAddress.getLocalHost().getHostAddress();
        instance = ip + "_" + port;
        skeltonMap.keySet().forEach(this::registryServer);
    }
    @PreDestroy
    public void unloadRegistryCenter() {
        skeltonMap.keySet().forEach(this::unregistryServer);
    }
    private void unregistryServer(String serverName) {
        RegistryCenter rc = applicationContext.getBean(RegistryCenter.class);
        rc.unregister(serverName,instance);
    }
    private void registryServer(String serverName) {
        RegistryCenter rc = applicationContext.getBean(RegistryCenter.class);
        rc.register(serverName,instance);
    }

    /**
     * 从服务提供者嗲用服务方法
     *
     * @param rpcRequest 调用对象
     * @return RpcResponse 调用结果
     */
    public RpcResponse<Object> invoke(RpcRequest rpcRequest) {
        RpcResponse<Object> rpcResponse = RpcResponse.builder().build();
        Map<String,ProviderMeta> providerMetaMap = skeltonMap.get(rpcRequest.getService());
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
