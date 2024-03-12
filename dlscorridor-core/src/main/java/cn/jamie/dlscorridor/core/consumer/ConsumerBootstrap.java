package cn.jamie.dlscorridor.core.consumer;

import cn.jamie.dlscorridor.core.annotation.JMConsumer;
import cn.jamie.dlscorridor.core.util.RpcReflectUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消费者获取服务代理
 */
@Data
@Slf4j
public class ConsumerBootstrap implements ApplicationContextAware {
    ApplicationContext applicationContext;

    private Map<String,Object> stub = new HashMap<>();

    /**
     * 解析消费者中需要服务提供者提供注入的服务实例
     */
    public void loadConsumerProxy() {
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        // 扫描服务提供者的类 跳过其他框架的bean
        Arrays.stream(beanNames).filter(x -> !x.startsWith("java.") && !x.startsWith("org.springframework"))
            .forEach( beanName ->  {
                Object bean = applicationContext.getBean(beanName);
                List<Field> fields = RpcReflectUtil.findAnnotationFields(bean.getClass(),JMConsumer.class);
                fields.forEach(f -> {
                    Class<?> service = f.getType();
                    Object consumer = stub.getOrDefault(service.getCanonicalName(),createConsumer(service));
                    f.setAccessible(true);
                    try {
                        f.set(bean, consumer);
                        stub.put(service.getCanonicalName(),consumer);
                    } catch (IllegalAccessException e) {
                        log.error(e.getMessage(),e);
                    }
                });
            });
    }

    private Object createConsumer(Class<?> service) {
        return Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service},new JMInvocationHandler(service));
    }
}
