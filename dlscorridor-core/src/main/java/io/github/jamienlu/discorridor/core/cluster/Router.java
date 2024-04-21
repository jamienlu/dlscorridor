package io.github.jamienlu.discorridor.core.cluster;

import io.github.jamienlu.discorridor.common.meta.InstanceMeta;

import java.util.List;

/**
 * @author jamieLu
 * @create 2024-03-17
 */
public interface Router {
    List<InstanceMeta> router(List<InstanceMeta> instanceMetas);

    Router Default = instanceMetas -> instanceMetas;
}
