package cn.jamie.dlscorridor.core.registry;

import cn.jamie.dlscorridor.core.meta.InstanceMeta;
import cn.jamie.dlscorridor.core.meta.ServiceMeta;

import java.util.List;
import java.util.Map;

/**
 * @author jamieLu
 * @create 2024-03-29
 */
public interface RegistryStorage {
    /**
     * 保存服务元数据
     *
     * @param service 服务元数据
     */
    void saveServiceMeta(ServiceMeta service);

    /**
     * 移除服务元数据
     *
     * @param servicePath 服务元数据路径
     */
    void removeServiceMeta(String servicePath);

    /**
     * 保存服务实例信息
     *
     * @param service 服务元数据
     * @param instanceMetas 版本实例信息
     */
    void saveServiceInstanceMetas(ServiceMeta service, Map<String, List<InstanceMeta>> instanceMetas);

    /**
     * 删除服务实例信息
     *
     * @param servicePath 服务元数据路径
     */
    void removeServiceInstanceMetas(String servicePath);

    /**
     * 查询服务实例信息
     *
     * @param service 服务元数据
     * @return List<InstanceMeta>
     */
    List<InstanceMeta> searchInstanceMetas(ServiceMeta service);

    /**
     * 查询所有服务元数据
     *
     * @return List<ServiceMeta>
     */
    List<ServiceMeta> findAllServiceMetas();

    /**
     * 清除数据
     */
    void cleanUp();
}
