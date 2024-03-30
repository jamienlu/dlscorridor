package cn.jamie.dlscorridor.core.registry;

import cn.jamie.dlscorridor.core.meta.InstanceMeta;
import cn.jamie.dlscorridor.core.meta.ServiceMeta;

import java.util.List;
import java.util.Map;

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
     * @param instanceMetas 服务版本 和 其实例数据
     */
    void onSubscribe(ServiceMeta serviceMeta, Map<String,List<InstanceMeta>> instanceMetas);
    /**
     * 反订阅事件
     *
     * @param serviceMeta 服务元数据
     */
    void onUnSubscribe(ServiceMeta serviceMeta);
}
