package cn.jamie.dlscorridor.core.cluster;

import cn.jamie.dlscorridor.core.meta.InstanceMeta;

import java.util.List;

/**
 * @author jamieLu
 * @create 2024-03-17
 */
public interface LoadBalancer {
    InstanceMeta choose(List<InstanceMeta> instanceMetas);
}
