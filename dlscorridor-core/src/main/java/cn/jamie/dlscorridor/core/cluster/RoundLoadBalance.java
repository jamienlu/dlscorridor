package cn.jamie.dlscorridor.core.cluster;

import cn.jamie.dlscorridor.core.api.LoadBalancer;
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
    public <T> T choose(List<T> providers) {
        if (CollectionUtils.isEmpty(providers)) {
            return null;
        } else if (providers.size() == 1) {
            return providers.get(0);
        } else {
            return  providers.get(index.getAndIncrement() & Integer.MAX_VALUE % providers.size());
        }
    }
}
