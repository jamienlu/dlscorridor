package cn.jamie.discorridor.spring.boot.autoconfigure;

import cn.jamie.discorridor.spring.boot.autoconfigure.bean.CustomerEnv;
import cn.jamie.dlscorridor.core.api.LoadBalancer;
import cn.jamie.dlscorridor.core.api.Router;
import cn.jamie.dlscorridor.core.api.RpcContext;
import cn.jamie.dlscorridor.core.cluster.RandomLoadBalance;
import cn.jamie.dlscorridor.core.cluster.RoundLoadBalance;
import cn.jamie.dlscorridor.core.consumer.ConsumerBootstrap;
import cn.jamie.dlscorridor.core.filter.CacheFilter;
import cn.jamie.dlscorridor.core.filter.FilterChain;
import cn.jamie.dlscorridor.core.filter.RpcFilterChain;
import cn.jamie.dlscorridor.core.filter.TokenFilter;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.jamie.discorridor.spring.boot.autoconfigure.constant.AutoConfigurationConst.CONSUMER_PREFIX;
import static cn.jamie.discorridor.spring.boot.autoconfigure.constant.AutoConfigurationConst.FILTER_CACHE;
import static cn.jamie.discorridor.spring.boot.autoconfigure.constant.AutoConfigurationConst.FILTER_TOKEN;
import static cn.jamie.discorridor.spring.boot.autoconfigure.constant.AutoConfigurationConst.LOADBALANCE_RANDOM;
import static cn.jamie.discorridor.spring.boot.autoconfigure.constant.AutoConfigurationConst.LOADBALANCE_ROUND;

/**
 * @author jamieLu
 * @create 2024-03-25
 */
@Configuration
@ConditionalOnProperty(prefix = CONSUMER_PREFIX, name = "enable")
@ConfigurationProperties(prefix = CONSUMER_PREFIX)
@AutoConfigureAfter({DiscorridorAutoConfigure.class, RegistryConfiguration.class})
@Data
public class ConsumerAutoConfigure {
    @NestedConfigurationProperty
    private CustomerEnv stub;
    @Bean
    public Router router() {
        return Router.Default;
    }
    @Bean
    public LoadBalancer loadBalancer() {
        String balance = stub.getLoadbalance();
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
        List<String> filters = Arrays.stream(stub.getFilters().split(";",-1)).toList();
        filters.forEach(x -> {
            if (x.equals(FILTER_TOKEN)) {
                filterChain.addFilter(new TokenFilter(stub.getTokenSize(),stub.getTokenRate()));
            }
            if (x.equals(FILTER_CACHE)) {
                filterChain.addFilter(new CacheFilter());
            }
        });
        return filterChain;
    }
    @Bean
    public RpcContext rpcContext(@Autowired Router router,@Autowired LoadBalancer loadBalancer,@Autowired FilterChain filterChain) {
        Map<String,String> parameters = new HashMap<>();
        parameters.put("app.retry", String.valueOf(stub.getRetry()));
        parameters.put("app.timeout", String.valueOf(stub.getTimeout()));
        parameters.put("app.faultLimit", String.valueOf(stub.getFaultLimit()));
        parameters.put("app.halfOpenDelay", String.valueOf(stub.getHalfOpenDelay()));
        parameters.put("app.halfOpenInitialDelay", String.valueOf(stub.getHalfOpenInitialDelay()));
        return RpcContext.builder().router(router).loadBalancer(loadBalancer).filterChain(filterChain).parameters(parameters).build();
    }
    @Bean
    @Order(Integer.MIN_VALUE)
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
