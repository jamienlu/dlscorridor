package io.github.jamienlu.discorridor.core.cluster;

import io.github.jamienlu.discorridor.core.meta.InstanceMeta;
import org.apache.commons.collections.CollectionUtils;

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
        if (CollectionUtils.isEmpty(providers)) {
            return null;
        } else if (providers.size() == 1) {
            return providers.get(0);
        } else {
            return  providers.get(index.getAndIncrement() & Integer.MAX_VALUE % providers.size());
        }
    }
}
