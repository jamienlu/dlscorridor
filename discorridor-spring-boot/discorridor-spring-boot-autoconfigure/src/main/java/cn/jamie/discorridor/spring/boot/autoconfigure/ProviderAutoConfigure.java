package cn.jamie.discorridor.spring.boot.autoconfigure;

import cn.jamie.dlscorridor.core.constant.MetaConstant;
import cn.jamie.dlscorridor.core.meta.InstanceMeta;
import cn.jamie.dlscorridor.core.provider.ProviderBootstrap;
import cn.jamie.dlscorridor.core.provider.ProviderInvoker;
import cn.jamie.dlscorridor.core.provider.ProviderNettyServer;
import cn.jamie.dlscorridor.core.provider.ProviderStorage;
import cn.jamie.dlscorridor.core.serialization.SerializationService;
import cn.jamie.dlscorridor.core.transform.netty.NettyConf;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
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
    private int port = 8080;
    private boolean gray = false;
    @NestedConfigurationProperty
    private NettyConf netty = new NettyConf();
    @Bean
    public ProviderStorage providerStorage() {
        return new ProviderStorage();
    }
    @Bean
    public ProviderBootstrap providerBootstrap(ProviderStorage providerStorage) {
        return new ProviderBootstrap(providerStorage);
    }
    @Bean
    public ProviderInvoker providerInvoker(ProviderStorage providerStorage) {
        return new ProviderInvoker(providerStorage);
    }
    @Bean
    @ConditionalOnProperty(prefix = TRANSFORM_NETTY, name = "enable")
    @ConditionalOnBean(SerializationService.class)
    public ProviderNettyServer providerNettyServer(@Autowired ProviderInvoker providerInvoker, @Autowired SerializationService serializationService) {
        return new ProviderNettyServer(providerInvoker, netty, serializationService);
    }

    @Bean
    public InstanceMeta instanceMeta(@Autowired(required = false) ProviderNettyServer providerNettyServer) {
        String ip;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        InstanceMeta instanceMeta = InstanceMeta.builder().host(ip).port(port).build();
        if (gray) {
            instanceMeta.getParameters().put(MetaConstant.GRAY, "true");
        }
        if (providerNettyServer != null) {
            instanceMeta.getParameters().put("port", String.valueOf(netty.getPort()));
        }
        return instanceMeta;
    }
}
