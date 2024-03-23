package cn.jamie.dlscorridor.core.registry;

import cn.jamie.dlscorridor.core.meta.InstanceMeta;
import cn.jamie.dlscorridor.core.meta.ServiceMeta;

import java.util.List;

/**
 * @author jamieLu
 * @create 2024-03-17
 */
public interface RegistryCenter {

    void start();
    void stop();
    // provicer
    void register(ServiceMeta service, InstanceMeta instance);
    void unregister(ServiceMeta service,InstanceMeta instance);
    //consumer
    List<InstanceMeta> fectchAll(ServiceMeta service);
    void subscribe(ServiceMeta service);
    void unsubscribe(ServiceMeta service);

}
