package cn.jamie.discorridor.spring.boot.autoconfigure;

import cn.jamie.discorridor.spring.boot.autoconfigure.bean.FaultEnv;
import cn.jamie.discorridor.spring.boot.autoconfigure.bean.FilterEnv;
import cn.jamie.discorridor.spring.boot.autoconfigure.bean.GrayEnv;
import cn.jamie.discorridor.spring.boot.autoconfigure.bean.LoadBalanceEnv;
import cn.jamie.dlscorridor.core.cluster.LoadBalancer;
import cn.jamie.dlscorridor.core.cluster.Router;
import cn.jamie.dlscorridor.core.api.RpcContext;
import cn.jamie.dlscorridor.core.cluster.GrayRouter;
import cn.jamie.dlscorridor.core.cluster.RandomLoadBalance;
import cn.jamie.dlscorridor.core.cluster.RoundLoadBalance;
import cn.jamie.dlscorridor.core.consumer.ConsumerBootstrap;
import cn.jamie.dlscorridor.core.filter.CacheFilter;
import cn.jamie.dlscorridor.core.filter.FilterChain;
import cn.jamie.dlscorridor.core.filter.RpcContextFilter;
import cn.jamie.dlscorridor.core.filter.RpcFilterChain;
import cn.jamie.dlscorridor.core.filter.TokenFilter;

import cn.jamie.dlscorridor.core.serialization.SerializationService;
import cn.jamie.dlscorridor.core.transform.http.HttpRpcTransform;
import cn.jamie.dlscorridor.core.transform.netty.NettyRpcTransform;
import cn.jamie.dlscorridor.core.transform.RpcTransform;
import cn.jamie.dlscorridor.core.transform.http.HttpConf;
import cn.jamie.dlscorridor.core.transform.netty.NettyConf;
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

import static cn.jamie.discorridor.spring.boot.autoconfigure.constant.AutoConfigurationConst.CONSUMER_PREFIX;
import static cn.jamie.discorridor.spring.boot.autoconfigure.constant.AutoConfigurationConst.FILTER_CACHE;
import static cn.jamie.discorridor.spring.boot.autoconfigure.constant.AutoConfigurationConst.FILTER_CONTEXT;
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
    private List<FilterEnv> filters = new ArrayList<>();
    @NestedConfigurationProperty
    private GrayEnv gray = new GrayEnv();
    @NestedConfigurationProperty
    private LoadBalanceEnv balance = new LoadBalanceEnv();
    @NestedConfigurationProperty
    private FaultEnv fault = new FaultEnv();
    @NestedConfigurationProperty
    private NettyConf netty = new NettyConf();
    @NestedConfigurationProperty
    private HttpConf http = new HttpConf();
    private String transform = "http";
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
    public RpcTransform rpcTransform(@Autowired SerializationService serializationService) {
        if ("http".equals(transform)) {
            return new HttpRpcTransform(http);
        } else if ("netty".equals(transform)) {
            return new NettyRpcTransform(netty,serializationService);
        } else {
            return new HttpRpcTransform(http);
        }
    }
    @Bean
    public RpcContext rpcContext(@Autowired Router router,@Autowired LoadBalancer loadBalancer,@Autowired FilterChain filterChain,
        @Autowired RpcTransform rpcTransform) {
        Map<String,String> parameters = new HashMap<>();
        parameters.put("app.retry", String.valueOf(fault.getRetry()));
        parameters.put("app.timeout", String.valueOf(fault.getTimeout()));
        parameters.put("app.faultLimit", String.valueOf(fault.getFaultLimit()));
        parameters.put("app.halfOpenDelay", String.valueOf(fault.getHalfOpenDelay()));
        parameters.put("app.halfOpenInitialDelay", String.valueOf(fault.getHalfOpenInitialDelay()));
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
