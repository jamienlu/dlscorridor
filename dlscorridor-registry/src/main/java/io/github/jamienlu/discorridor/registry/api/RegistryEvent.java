package io.github.jamienlu.discorridor.registry.api;

import io.github.jamienlu.discorridor.common.api.Event;
import io.github.jamienlu.discorridor.common.meta.InstanceMeta;
import io.github.jamienlu.discorridor.common.meta.ServiceMeta;

import java.util.List;

/**
 * @author jamieLu
 * @create 2024-04-06
 */
public interface RegistryEvent extends Event {

    /**
     * 保存服务元数据
     *
     * @param service 服务元数据
     */
    void saveServiceMeta(ServiceMeta service);

    /**
     * 移除服务元数据
     *
     * @param service 服务元数据
     */
    void removeServiceMeta(ServiceMeta service);

    /**
     * 保存服务实例信息
     *
     * @param service 服务元数据
     * @param instanceMetas 实例信息
     */
    void saveServiceInstanceMetas(ServiceMeta service, List<InstanceMeta> instanceMetas);

    /**
     * 删除服务实例信息
     *
     * @param service 服务元数据
     */
    void removeServiceInstanceMetas(ServiceMeta service);

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
