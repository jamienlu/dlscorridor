package cn.jamie.dlscorridor.core.cluster;

import cn.jamie.dlscorridor.core.api.LoadBalancer;
import org.apache.commons.collections.CollectionUtils;

import java.security.SecureRandom;
import java.util.List;

/**
 * @author jamieLu
 * @create 2024-03-17
 */
public class RandomLoadBalance implements LoadBalancer {
    SecureRandom random = new SecureRandom();
    @Override
    public <T> T choose(List<T> providers) {
        if (CollectionUtils.isEmpty(providers)) {
            return null;
        } else if (providers.size() == 1) {
            return providers.get(0);
        } else {
            return  providers.get(random.nextInt(providers.size()));
        }
    }
}
