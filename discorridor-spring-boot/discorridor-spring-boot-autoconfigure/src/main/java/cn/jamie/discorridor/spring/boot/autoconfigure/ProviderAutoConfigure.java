package cn.jamie.discorridor.spring.boot.autoconfigure;

import cn.jamie.dlscorridor.core.meta.InstanceMeta;
import cn.jamie.dlscorridor.core.meta.ServiceMeta;
import cn.jamie.dlscorridor.core.provider.ProviderBootstrap;
import cn.jamie.dlscorridor.core.provider.ProviderInvoker;
import cn.jamie.dlscorridor.core.provider.ProviderNettyServer;
import cn.jamie.dlscorridor.core.provider.ProviderStorage;
import cn.jamie.dlscorridor.core.registry.RegistryCenter;
import cn.jamie.dlscorridor.core.serialization.SerializationService;
import cn.jamie.dlscorridor.core.transform.netty.NettyConf;
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

import static cn.jamie.discorridor.spring.boot.autoconfigure.constant.AutoConfigurationConst.PROVIDER_PREFIX;
import static cn.jamie.discorridor.spring.boot.autoconfigure.constant.AutoConfigurationConst.TRANSFORM_NETTY;

/**
 * @author jamieLu
 * @create 2024-03-25
 */
@Configuration
@ConditionalOnProperty(prefix = PROVIDER_PREFIX, name = "enable")
@ConfigurationProperties(prefix = PROVIDER_PREFIX)
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
    @ConditionalOnProperty(prefix = TRANSFORM_NETTY, name = "enable")
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
