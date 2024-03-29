package cn.jamie.dlscorridor.core.registry;

import cn.jamie.dlscorridor.core.meta.InstanceMeta;
import cn.jamie.dlscorridor.core.meta.ServiceMeta;

import java.util.List;

/**
 * @author jamieLu
 * @create 2024-03-29
 */
public interface RegistryStorage {
    void saveServiceMeta(ServiceMeta service);
    void removeServiceMeta(String serviceName);
    void saveServiceInstanceMetas(ServiceMeta service, List<InstanceMeta> instanceMetas);
    void removeServiceInstanceMetas(String serviceName);
    List<InstanceMeta> searchInstanceMetas(String serviceName);
    List<ServiceMeta> findAllServiceMetas();
    void cleanUp();
}
