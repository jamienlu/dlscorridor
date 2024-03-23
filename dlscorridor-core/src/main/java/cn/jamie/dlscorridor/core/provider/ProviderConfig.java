package cn.jamie.dlscorridor.core.provider;

import cn.jamie.dlscorridor.core.meta.ServiceMeta;
import cn.jamie.dlscorridor.core.registry.RegistryCenter;
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
public class ProviderConfig {
    @Value("${app.id:discorridor-provider}")
    private String app;
    @Value("${app.env:dev}")
    private String env;
    @Value("${app.namespace:discorridorr}")
    private String namaspace;
    private String name;
    @Value("${app.version:1.0}")
    private String version;
    @Bean
    ProviderBootstrap providerBootstrap() {
        return new ProviderBootstrap();
    }
    @Bean
    public ServiceMeta serviceMeta() {
        return ServiceMeta.builder().app(app).env(env).namespace(namaspace).version(version).build();
    }
    @Bean
    public ZkRegistryCenterListener zkRegistryCenterListener() {
        return new ZkRegistryCenterListener();
    }
    @Bean
    ProviderInvoker providerInvoker(@Autowired ProviderBootstrap providerBootstrap) {
        return new ProviderInvoker(providerBootstrap);
    }
    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner providerStartRegistryCenter(@Autowired ProviderBootstrap providerBootstrap) {
        return x -> providerBootstrap.registryRegistryCenter();
    }
    @Bean
    public ZkEnvData zkEnvData() {
        return ZkEnvData.builder().namespace("discorridor").url("192.168.0.101:2181").baseTime(1000).maxRetries(3).build();
    }
    // 创建bean后启动zk
    @Bean(initMethod = "start")
    public RegistryCenter providerRegistryCenter(@Autowired ZkEnvData zkEnvData) {
        return new ZkRegistryCenter(zkEnvData);
    }
}
