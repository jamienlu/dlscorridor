package io.github.jamienlu.discorridor.spring.boot.autoconfigure;

import io.github.jamienlu.discorridor.spring.boot.autoconfigure.bean.FaultConf;
import io.github.jamienlu.discorridor.spring.boot.autoconfigure.bean.FilterConf;
import io.github.jamienlu.discorridor.spring.boot.autoconfigure.bean.GrayConf;
import io.github.jamienlu.discorridor.spring.boot.autoconfigure.bean.LoadBalanceConf;
import io.github.jamienlu.discorridor.core.cluster.LoadBalancer;
import io.github.jamienlu.discorridor.core.cluster.Router;
import io.github.jamienlu.discorridor.core.api.RpcContext;
import io.github.jamienlu.discorridor.core.cluster.GrayRouter;
import io.github.jamienlu.discorridor.core.cluster.RandomLoadBalance;
import io.github.jamienlu.discorridor.core.cluster.RoundLoadBalance;
import io.github.jamienlu.discorridor.core.consumer.ConsumerBootstrap;
import io.github.jamienlu.discorridor.core.filter.CacheFilter;
import io.github.jamienlu.discorridor.core.filter.FilterChain;
import io.github.jamienlu.discorridor.core.filter.RpcContextFilter;
import io.github.jamienlu.discorridor.core.filter.RpcFilterChain;
import io.github.jamienlu.discorridor.core.filter.TokenFilter;

import io.github.jamienlu.discorridor.serialization.api.SerializationService;
import io.github.jamienlu.transform.http.HttpRpcTransform;
import io.github.jamienlu.transform.netty.NettyRpcTransform;
import io.github.jamienlu.transform.api.RpcTransform;
import io.github.jamienlu.transform.http.HttpConf;
import io.github.jamienlu.transform.netty.NettyConf;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.jamienlu.discorridor.spring.boot.autoconfigure.constant.AutoConfigurationConst.CONSUMER_PREFIX;
import static io.github.jamienlu.discorridor.spring.boot.autoconfigure.constant.AutoConfigurationConst.FILTER_CACHE;
import static io.github.jamienlu.discorridor.spring.boot.autoconfigure.constant.AutoConfigurationConst.FILTER_CONTEXT;
import static io.github.jamienlu.discorridor.spring.boot.autoconfigure.constant.AutoConfigurationConst.FILTER_TOKEN;
import static io.github.jamienlu.discorridor.spring.boot.autoconfigure.constant.AutoConfigurationConst.LOADBALANCE_RANDOM;
import static io.github.jamienlu.discorridor.spring.boot.autoconfigure.constant.AutoConfigurationConst.LOADBALANCE_ROUND;
import static io.github.jamienlu.discorridor.spring.boot.autoconfigure.constant.AutoConfigurationConst.RPC_FAULT_LIMIT;
import static io.github.jamienlu.discorridor.spring.boot.autoconfigure.constant.AutoConfigurationConst.RPC_HALF_DELAY;
import static io.github.jamienlu.discorridor.spring.boot.autoconfigure.constant.AutoConfigurationConst.RPC_HALF_INIT_DELAY;
import static io.github.jamienlu.discorridor.spring.boot.autoconfigure.constant.AutoConfigurationConst.RPC_HTTP;
import static io.github.jamienlu.discorridor.spring.boot.autoconfigure.constant.AutoConfigurationConst.RPC_NETTY;
import static io.github.jamienlu.discorridor.spring.boot.autoconfigure.constant.AutoConfigurationConst.RPC_RETRY;
import static io.github.jamienlu.discorridor.spring.boot.autoconfigure.constant.AutoConfigurationConst.RPC_TIMEOUT;

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
    private List<FilterConf> filters = new ArrayList<>();
    @NestedConfigurationProperty
    private GrayConf gray = new GrayConf();
    @NestedConfigurationProperty
    private LoadBalanceConf balance = new LoadBalanceConf();
    @NestedConfigurationProperty
    private FaultConf fault = new FaultConf();
    private String transform = RPC_HTTP;
    @Bean
    public Router router() {
        if (gray.getEnable()) {
            return new GrayRouter(gray.getRatio());
        }
        return Router.Default;
    }
    @Bean
    public LoadBalancer loadBalancer() {
        if (LOADBALANCE_ROUND.equals(balance.getType())) {
            return new RoundLoadBalance();
        } else if (LOADBALANCE_RANDOM.equals(balance.getType())) {
            return new RandomLoadBalance();
        } else {
            return instanceMetas -> instanceMetas.get(0);
        }
    }

    @Bean
    public FilterChain filters() {
        FilterChain filterChain = new RpcFilterChain();
        filters.forEach(filter -> {
            if (FILTER_TOKEN.equals(filter.getType())) {
                filterChain.addFilter(new TokenFilter(filter.getTokenSize(),filter.getTokenSeconds()));
            }
            if (FILTER_CONTEXT.equals(filter.getType())) {
                filterChain.addFilter(new RpcContextFilter());
            }
            if (FILTER_CACHE.equals(filter.getType())) {
                filterChain.addFilter(new CacheFilter(filter.getCacheSize(), filter.getCacheSeconds()));
            }
        });
        return filterChain;
    }
    @Bean
    @ConditionalOnBean(SerializationService.class)
    public RpcTransform rpcTransform(@Autowired SerializationService serializationService, @Autowired NettyConf netty, @Autowired HttpConf http) {
        if (RPC_HTTP.equals(transform)) {
            return new HttpRpcTransform(http, serializationService);
        } else if (RPC_NETTY.equals(transform) && netty != null) {
            return new NettyRpcTransform(netty, serializationService);
        } else {
            return new HttpRpcTransform(http, serializationService);
        }
    }
    @Bean
    public RpcContext rpcContext(@Autowired Router router,@Autowired LoadBalancer loadBalancer,@Autowired FilterChain filterChain,
        @Autowired RpcTransform rpcTransform) {
        Map<String,String> parameters = new HashMap<>();
        parameters.put(RPC_RETRY, String.valueOf(fault.getRetry()));
        parameters.put(RPC_TIMEOUT, String.valueOf(fault.getTimeout()));
        parameters.put(RPC_FAULT_LIMIT, String.valueOf(fault.getFaultLimit()));
        parameters.put(RPC_HALF_DELAY, String.valueOf(fault.getHalfOpenDelay()));
        parameters.put(RPC_HALF_INIT_DELAY, String.valueOf(fault.getHalfOpenInitialDelay()));
        return RpcContext.builder().router(router).loadBalancer(loadBalancer).filterChain(filterChain).transform(rpcTransform).parameters(parameters).build();
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
