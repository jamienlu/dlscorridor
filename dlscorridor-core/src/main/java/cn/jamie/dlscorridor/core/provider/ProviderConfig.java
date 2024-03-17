package cn.jamie.dlscorridor.core.provider;

import cn.jamie.dlscorridor.core.api.RegistryCenter;
import cn.jamie.dlscorridor.core.registry.ZkRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class ProviderConfig {
    @Bean
    ProviderBootstrap providerBootstrap() {
        return new ProviderBootstrap();
    }
    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner providerStartRegistryCenter(@Autowired ProviderBootstrap providerBootstrap) {
        return x -> providerBootstrap.startRegistryCenter();
    }
    @Bean(initMethod = "start", destroyMethod = "stop")
    public RegistryCenter providerRegistryCenter() {
        return new ZkRegistryCenter();
    }
}
