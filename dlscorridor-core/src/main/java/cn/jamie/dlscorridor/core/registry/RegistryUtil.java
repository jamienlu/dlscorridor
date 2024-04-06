package cn.jamie.dlscorridor.core.registry;

import cn.jamie.dlscorridor.core.meta.ServiceMeta;

/**
 * @author jamieLu
 * @create 2024-04-06
 */
public class RegistryUtil {
    public static String serviceMetaKey(ServiceMeta serviceMeta) {
        return serviceMeta.getGroup() + "-" + serviceMeta.getName();
    }
}
