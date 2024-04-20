package io.github.jamienlu.discorridor.core.registry;

import io.github.jamienlu.discorridor.core.meta.InstanceMeta;
import io.github.jamienlu.discorridor.core.meta.ServiceMeta;

import java.util.List;

/**
 * @author jamieLu
 * @create 2024-03-18
 */
public interface RegistryCenterListener {
    /**
     * 注册事件
     *
     * @param serviceMeta 服务元数据
     */
    void onRegistry(ServiceMeta serviceMeta);
    /**
     * 反注册事件
     *
     * @param serviceMeta 服务元数据
     */
    void onUnRegistry(ServiceMeta serviceMeta);

    /**
     * 订阅事件
     *
     * @param serviceMeta 服务元数据
     * @param instanceMetas 实例数据
     */
    void onSubscribe(ServiceMeta serviceMeta,  List<InstanceMeta> instanceMetas);
    /**
     * 反订阅事件
     *
     * @param serviceMeta 服务元数据
     */
    void onUnSubscribe(ServiceMeta serviceMeta);
}
