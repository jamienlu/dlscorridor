package io.github.jamienlu.discorridor.core.registry;

import io.github.jamienlu.discorridor.core.meta.ServiceMeta;

/**
 * @author jamieLu
 * @create 2024-04-06
 */
public class RegistryUtil {
    public static String serviceMetaKey(ServiceMeta serviceMeta) {
        return serviceMeta.getGroup() + "-" + serviceMeta.getName();
    }
}
