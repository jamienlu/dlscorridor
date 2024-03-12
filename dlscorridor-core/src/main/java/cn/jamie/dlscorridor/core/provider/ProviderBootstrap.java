package cn.jamie.dlscorridor.core.provider;

import cn.jamie.dlscorridor.core.annotation.JMProvider;
import cn.jamie.dlscorridor.core.annotation.RpcService;
import cn.jamie.dlscorridor.core.api.RpcRequest;
import cn.jamie.dlscorridor.core.api.RpcResponse;
import cn.jamie.dlscorridor.core.util.RpcMethodUtil;
import cn.jamie.dlscorridor.core.util.RpcReflectUtil;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Method;
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

    private Map<String,Object> skeltonMap = new HashMap<>();
    @PostConstruct
    /**
     * 映射接口和实现类
     */
    public void buildProviders() {
        Map<String,Object> beanMap = applicationContext.getBeansWithAnnotation(JMProvider.class);
        beanMap.forEach((name,bean) -> System.out.println(name));
        beanMap.values().forEach(implBean -> RpcReflectUtil.findAnnotationInterfaces(implBean.getClass(), RpcService.class)
                .forEach(intefaceClass -> skeltonMap.put(intefaceClass.getCanonicalName(),implBean)));
    }

    /**
     * 从服务提供者嗲用服务方法
     *
     * @param rpcRequest 调用对象
     * @return RpcResponse 调用结果
     */
    public RpcResponse<Object> invoke(RpcRequest rpcRequest) {
        RpcResponse<Object> rpcResponse = RpcResponse.builder().build();
        Object skelton = skeltonMap.get(rpcRequest.getService());
        Object data = null;
        try {
            Method method = RpcReflectUtil.findMethod(skelton.getClass(), rpcRequest.getMethodName());
            // json 序列化还原  数组和集合类型数据处理
            Object[] realArgs = new Object[method.getParameterTypes().length];
            for (int i = 0; i < realArgs.length; i++) {
                realArgs[i] = JSON.parseObject(JSON.toJSONString(rpcRequest.getArgs()[i]),method.getParameterTypes()[i]);
            }
            data = method.invoke(skelton, realArgs);
            rpcResponse.setData(data);
            rpcResponse.setStatus(true);
        } catch (Exception e) {
            rpcResponse.setStatus(false);
            rpcResponse.setEx(e);
        }
        return rpcResponse;
    }
}
