package cn.jamie.discorridor.spring.boot.autoconfigure;

import cn.jamie.discorridor.spring.boot.autoconfigure.bean.CustomerEnv;
import cn.jamie.dlscorridor.core.api.LoadBalancer;
import cn.jamie.dlscorridor.core.api.Router;
import cn.jamie.dlscorridor.core.cluster.RandomLoadBalance;
import cn.jamie.dlscorridor.core.cluster.RoundLoadBalance;
import cn.jamie.dlscorridor.core.consumer.ConsumerBootstrap;
import cn.jamie.dlscorridor.core.filter.CacheFilter;
import cn.jamie.dlscorridor.core.filter.Filter;
import cn.jamie.dlscorridor.core.filter.FilterChain;
import cn.jamie.dlscorridor.core.filter.RpcFilterChain;
import cn.jamie.dlscorridor.core.filter.TokenFilter;
import cn.jamie.dlscorridor.core.meta.InstanceMeta;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static cn.jamie.discorridor.spring.boot.autoconfigure.constant.AutoConfigurationConst.CONSUMER_PREFIX;
import static cn.jamie.discorridor.spring.boot.autoconfigure.constant.AutoConfigurationConst.DISCORRIDOR_PREFIX;
import static cn.jamie.discorridor.spring.boot.autoconfigure.constant.AutoConfigurationConst.LOADBALANCE_RANDOM;
import static cn.jamie.discorridor.spring.boot.autoconfigure.constant.AutoConfigurationConst.LOADBALANCE_ROUND;
import static cn.jamie.discorridor.spring.boot.autoconfigure.constant.AutoConfigurationConst.REGISTRY_PREFIX;

/**
 * @author jamieLu
 * @create 2024-03-25
 */
@Configuration
@ConditionalOnProperty(prefix = CONSUMER_PREFIX, name = "enable")
@ConfigurationProperties(prefix = CONSUMER_PREFIX)
@AutoConfigureAfter(DiscorridorAutoConfigure.class)
@Data
public class ConsumerAutoConfigure {
    @NestedConfigurationProperty
    private CustomerEnv env;
    @Bean
    public Router router() {
        return Router.Default;
    }
    @Bean
    public LoadBalancer loadBalancer() {
        String balance = env.getLoadbalance();
        if (LOADBALANCE_ROUND.equals(balance)) {
            return new RoundLoadBalance();
        } else if (LOADBALANCE_RANDOM.equals(balance)) {
            return new RandomLoadBalance();
        } else {
            return instanceMetas -> instanceMetas.get(0);
        }
    }

    @Bean
    public FilterChain filters() {
        FilterChain filterChain = new RpcFilterChain();
        List<String> filters = Arrays.stream(env.getFilters().split(";",-1)).toList();
        filters.forEach(x -> {
            if (x.equals("token")) {
                filterChain.addFilter(new TokenFilter(100,10));
            }
            if (x.equals("cache")) {
                filterChain.addFilter(new CacheFilter());
            }
        });
        return filterChain;
    }

    @Bean
    public ConsumerBootstrap consumerBootstrap() {
        return  new ConsumerBootstrap();
    }

    @Bean
    @Order(Integer.MIN_VALUE + 1)
    public ApplicationRunner consumerBootstrap_runner(@Autowired ConsumerBootstrap consumerBootstrap) {
        return x -> {
            // 方法依赖其他bean得等待加载避免循环依赖
            consumerBootstrap.initStubs();
        };
    }

}
