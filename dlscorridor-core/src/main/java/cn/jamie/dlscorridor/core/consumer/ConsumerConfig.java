package cn.jamie.dlscorridor.core.consumer;

import cn.jamie.dlscorridor.core.api.LoadBalancer;
import cn.jamie.dlscorridor.core.api.RegistryCenter;
import cn.jamie.dlscorridor.core.api.Router;
import cn.jamie.dlscorridor.core.cluster.RandomLoadBalance;
import cn.jamie.dlscorridor.core.cluster.RoundLoadBalance;
import cn.jamie.dlscorridor.core.conf.DisCorridorConf;
import cn.jamie.dlscorridor.core.registry.ZkRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.List;

@Configuration
public class ConsumerConfig {
    @Bean
    ConsumerBootstrap consumerBootstrap() {
        return new ConsumerBootstrap();
    }
    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner consumerRunnerLoadProxy(@Autowired ConsumerBootstrap consumerBootstrap) {
        return x -> consumerBootstrap.loadConsumerProxy();
    }
    @Bean
    public LoadBalancer loadBalancer() {
        return new RoundLoadBalance();
    }
    @Bean
    public Router router() {
        return Router.Default;
    }

    /**
     * 注册中心注入
     * bean初始化和销毁 调用注册中的得初始化和销毁
     * @return RegistryCenter
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    public RegistryCenter consumerRegistryCenter() {
        return new ZkRegistryCenter();
    }
}
