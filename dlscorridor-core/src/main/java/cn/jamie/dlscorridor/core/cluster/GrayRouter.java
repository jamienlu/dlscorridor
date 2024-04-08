package cn.jamie.dlscorridor.core.cluster;

import cn.jamie.dlscorridor.core.constant.MetaConstant;
import cn.jamie.dlscorridor.core.meta.InstanceMeta;
import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 灰度路由 分配实例的时候对灰度分配定比流量
 *
 * @author jamieLu
 * @create 2024-04-03
 */
@Slf4j
public class GrayRouter implements Router {
    private final int grayRatio;
    private final SecureRandom random = new SecureRandom();

    public GrayRouter(int grayRatio) {
        this.grayRatio = grayRatio;
    }
    @Override
    public List<InstanceMeta> router(List<InstanceMeta> instanceMetas) {
        if (instanceMetas==null || instanceMetas.size() <= 1) {
            return instanceMetas;
        }
        List<InstanceMeta> grayInstances = instanceMetas.stream().filter(x -> x.getParameters().containsKey(MetaConstant.GRAY)).collect(Collectors.toList());
        List<InstanceMeta> normalInstances = instanceMetas.stream().filter(x -> !x.getParameters().containsKey(MetaConstant.GRAY)).collect(Collectors.toList());
        log.debug(" grayRouter grayNodes/normalNodes,grayRatio ===> {}/{},{}",
                grayInstances.size(), normalInstances.size(), grayRatio);
        if (grayInstances.isEmpty() || normalInstances.isEmpty()) {
            return instanceMetas;
        }
        if (grayRatio <= 0) {
            return normalInstances;
        } else if (grayRatio >= 100) {
            return grayInstances;
        } else {
            if (random.nextInt(100) < grayRatio) {
                log.debug("router use gray instances");
                return grayInstances;
            } else {
                log.debug("router use normal instances");
                return normalInstances;
            }
        }
    }
}
