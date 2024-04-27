package io.github.jamienlu.discorridor.spring.boot.autoconfigure;

import io.github.jamienlu.discorridor.common.meta.InstanceMeta;
import io.github.jamienlu.discorridor.common.meta.ServiceMeta;
import io.github.jamienlu.discorridor.core.provider.ProviderBootstrap;
import io.github.jamienlu.discorridor.core.provider.ProviderInvoker;
import io.github.jamienlu.transform.netty.ProviderNettyServer;
import io.github.jamienlu.discorridor.core.provider.ProviderStorage;
import io.github.jamienlu.discorridor.registry.api.RegistryCenter;
import io.github.jamienlu.discorridor.serialization.api.SerializationService;
import io.github.jamienlu.transform.netty.NettyConf;
import io.github.jamienlu.discorridor.spring.boot.autoconfigure.constant.AutoConfigurationConst;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author jamieLu
 * @create 2024-03-25
 */
@Configuration
@ConditionalOnProperty(prefix = AutoConfigurationConst.PROVIDER_PREFIX, name = "enable")
@ConfigurationProperties(prefix = AutoConfigurationConst.PROVIDER_PREFIX)
@AutoConfigureAfter({DiscorridorAutoConfigure.class, RegistryConfiguration.class})
@Data
public class ProviderAutoConfigure {
    @Value("${server.port}")
    private int port = 8080;
    @Bean
    public ProviderStorage providerStorage() {
        return new ProviderStorage();
    }
    @Bean(initMethod = "init", destroyMethod = "destroy")
    @ConditionalOnBean(RegistryCenter.class)
    public ProviderBootstrap providerBootstrap(ProviderStorage providerStorage) {
        return new ProviderBootstrap(providerStorage);
    }
    @Bean
    @ConditionalOnBean(ServiceMeta.class)
    public ProviderInvoker providerInvoker(@Autowired ProviderStorage providerStorage, @Autowired ServiceMeta serviceMeta) {
        return new ProviderInvoker(providerStorage, serviceMeta);
    }
    @Bean
    @ConditionalOnProperty(prefix = AutoConfigurationConst.TRANSFORM_NETTY, name = "enable")
    @ConditionalOnBean({SerializationService.class, NettyConf.class})
    public ProviderNettyServer providerNettyServer(@Autowired ProviderInvoker providerInvoker, @Autowired SerializationService serializationService
        , @Autowired NettyConf netty) {
        return new ProviderNettyServer(providerInvoker, netty, serializationService);
    }

    @Bean
    public InstanceMeta instanceMeta(@Autowired(required = false) NettyConf netty) {
        String ip;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        InstanceMeta instanceMeta = InstanceMeta.builder().host(ip).port(port).build();
        if (netty != null) {
            instanceMeta.getParameters().put("port", String.valueOf(netty.getPort()));
        }
        return instanceMeta;
    }
}
