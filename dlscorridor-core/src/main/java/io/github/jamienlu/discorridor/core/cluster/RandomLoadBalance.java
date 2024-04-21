package io.github.jamienlu.discorridor.core.cluster;

import io.github.jamienlu.discorridor.common.meta.InstanceMeta;

import java.security.SecureRandom;
import java.util.List;

/**
 * @author jamieLu
 * @create 2024-03-17
 */
public class RandomLoadBalance implements LoadBalancer {
    private final SecureRandom random = new SecureRandom();
    @Override
    public InstanceMeta choose(List<InstanceMeta> providers) {
        if (providers == null || providers.isEmpty()) {
            return null;
        } else if (providers.size() == 1) {
            return providers.get(0);
        } else {
            return providers.get(random.nextInt(providers.size()));
        }
    }
}
