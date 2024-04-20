package io.github.jamienlu.discorridor.core.registry;

import io.github.jamienlu.discorridor.core.meta.InstanceMeta;
import io.github.jamienlu.discorridor.core.meta.ServiceMeta;

import java.util.List;

/**
 * @author jamieLu
 * @create 2024-03-17
 */
public interface RegistryCenter {
    /**
     * 启动注册中心
     */
    void start();
    /**
     * 停止注册中心
     */
    void stop();

    /**
     * 注册中心注册服务
     *
     * @param service 服务
     * @param instance 实例
     */
    void register(ServiceMeta service, InstanceMeta instance);
    /**
     * 注册中心反注册服务
     *
     * @param service 服务
     * @param instance 实例
     */
    void unregister(ServiceMeta service,InstanceMeta instance);
    /**
     * 注册中心拉取服务的实例信息
     *
     * @param service 服务
     */
    List<InstanceMeta> fectchAll(ServiceMeta service);
    /**
     * 订阅服务的实例信息
     *
     * @param service 服务
     */
    void subscribe(ServiceMeta service);
    /**
     * 取消订阅服务的实例信息
     *
     * @param service 服务
     */
    void unsubscribe(ServiceMeta service);

}
