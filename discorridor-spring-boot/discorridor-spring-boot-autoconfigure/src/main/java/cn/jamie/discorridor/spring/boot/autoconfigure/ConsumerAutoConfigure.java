package cn.jamie.discorridor.spring.boot.autoconfigure;

import cn.jamie.dlscorridor.core.api.LoadBalancer;
import cn.jamie.dlscorridor.core.api.Router;
import cn.jamie.dlscorridor.core.cluster.RandomLoadBalance;
import cn.jamie.dlscorridor.core.cluster.RoundLoadBalance;
import cn.jamie.dlscorridor.core.filter.CacheFilter;
import cn.jamie.dlscorridor.core.filter.Filter;
import cn.jamie.dlscorridor.core.meta.InstanceMeta;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static cn.jamie.discorridor.spring.boot.autoconfigure.constant.AutoConfigurationConst.CONSUMER_PREFIX;
import static cn.jamie.discorridor.spring.boot.autoconfigure.constant.AutoConfigurationConst.LOADBALANCE_RANDOM;
import static cn.jamie.discorridor.spring.boot.autoconfigure.constant.AutoConfigurationConst.LOADBALANCE_ROUND;

/**
 * @author jamieLu
 * @create 2024-03-25
 */
@Configuration
@ConditionalOnProperty(prefix = CONSUMER_PREFIX, name = "enabled")
@AutoConfigureAfter(DiscorridorAutoConfigure.class)
public class ConsumerAutoConfigure {
    @Value("${loadbalance.type}")
    private String loadBalancerType;
    @Value("${filter}")
    private String filter;
    @Bean
    public Router router() {
        return Router.Default;
    }
    @Bean
    public LoadBalancer loadBalancer() {
        if (LOADBALANCE_ROUND.equals(loadBalancerType)) {
            return new RoundLoadBalance();
        } else if (LOADBALANCE_RANDOM.equals(loadBalancerType)) {
            return new RandomLoadBalance();
        } else {
            return instanceMetas -> instanceMetas.get(0);
        }
    }

    @Bean
    public List<Filter> filters() {
        // filter按顺序注入
        return null;
    }

}
