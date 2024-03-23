package cn.jamie.dlscorridor.core.registry;

import cn.jamie.dlscorridor.core.meta.InstanceMeta;
import cn.jamie.dlscorridor.core.meta.ServiceMeta;

import java.util.List;

/**
 * @author jamieLu
 * @create 2024-03-18
 */
public interface RegistryCenterListener {
    void onRegistry(ServiceMeta serviceMeta);

    void onUnRegistry(ServiceMeta serviceMeta);

    void onSubscribe(ServiceMeta serviceMeta, List<InstanceMeta> instanceMetas);

    void onUnSubscribe(ServiceMeta serviceMeta);
}
