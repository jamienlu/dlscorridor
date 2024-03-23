package cn.jamie.dlscorridor.core.consumer;

import cn.jamie.dlscorridor.core.api.LoadBalancer;
import cn.jamie.dlscorridor.core.meta.ServiceMeta;
import cn.jamie.dlscorridor.core.registry.RegistryCenter;
import cn.jamie.dlscorridor.core.api.Router;
import cn.jamie.dlscorridor.core.cluster.RoundLoadBalance;
import cn.jamie.dlscorridor.core.registry.zookeeper.ZkEnvData;
import cn.jamie.dlscorridor.core.registry.zookeeper.ZkRegistryCenter;
import cn.jamie.dlscorridor.core.registry.zookeeper.ZkRegistryCenterListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class ConsumerConfig {
    @Value("${app.id:discorridor-provider}")
    private String app;
    @Value("${app.env:dev}")
    private String env;
    @Value("${app.namespace:discorridorr}")
    private String namaspace;
    @Value("${app.name: test-provider}")
    private String name;
    @Value("${app.version:1.0}")
    private String version;
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
    @Bean
    public ZkEnvData zkEnvData() {
        return ZkEnvData.builder().namespace("discorridor").url("192.168.0.101:2181").baseTime(1000).maxRetries(3).build();
    }
    // 创建bean后启动zk
    @Bean(initMethod = "start")
    public RegistryCenter consumerRegistryCenter(@Autowired ZkEnvData zkEnvData) {
        return new ZkRegistryCenter(zkEnvData);
    }
    @Bean
    public ServiceMeta serviceMeta() {
        return ServiceMeta.builder().app(app).env(env).namespace(namaspace).name(name).version(version).build();
    }
    @Bean
    public ZkRegistryCenterListener zkRegistryCenterListener() {
        return new ZkRegistryCenterListener();
    }
}
