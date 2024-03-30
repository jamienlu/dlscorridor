package cn.jamie.discorridor.spring.boot.autoconfigure;

import cn.jamie.dlscorridor.core.meta.InstanceMeta;
import cn.jamie.dlscorridor.core.provider.ProviderBootstrap;
import cn.jamie.dlscorridor.core.provider.ProviderInvoker;
import cn.jamie.dlscorridor.core.provider.ProviderStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static cn.jamie.discorridor.spring.boot.autoconfigure.constant.AutoConfigurationConst.PROVIDER_PREFIX;

/**
 * @author jamieLu
 * @create 2024-03-25
 */
@Configuration
@ConditionalOnProperty(prefix = PROVIDER_PREFIX, name = "enable")
@AutoConfigureAfter(DiscorridorAutoConfigure.class)
public class ProviderAutoConfigure {
    @Value("${server.port}")
    private int port;
    @Bean
    public ProviderStorage providerStorage() {
        return new ProviderStorage();
    }
    @Bean
    @Order(Integer.MIN_VALUE + 1)
    public ProviderBootstrap providerBootstrap(ProviderStorage providerStorage) {
        return new ProviderBootstrap(providerStorage);
    }
    @Bean
    @Order(Integer.MIN_VALUE)
    public ProviderInvoker providerInvoker(ProviderStorage providerStorage) {
        return new ProviderInvoker(providerStorage);
    }
    @Bean
    public InstanceMeta instanceMeta() {
        String ip = "localhost";
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        return InstanceMeta.builder().host(ip).port(port).build();
    }
}
