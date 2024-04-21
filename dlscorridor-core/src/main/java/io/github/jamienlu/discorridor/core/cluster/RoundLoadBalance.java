package io.github.jamienlu.discorridor.core.cluster;

import io.github.jamienlu.discorridor.common.meta.InstanceMeta;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jamieLu
 * @create 2024-03-17
 */
public class RoundLoadBalance implements LoadBalancer {
    AtomicInteger index = new AtomicInteger(0);
    @Override
    public InstanceMeta choose(List<InstanceMeta> providers) {
        if (providers == null || providers.isEmpty()) {
            return null;
        } else if (providers.size() == 1) {
            return providers.get(0);
        } else {
            return  providers.get(index.getAndIncrement() & Integer.MAX_VALUE % providers.size());
        }
    }
}
