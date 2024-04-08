package cn.jamie.discorridor.spring.boot.autoconfigure;

import cn.jamie.discorridor.spring.boot.autoconfigure.bean.GrayEnv;
import cn.jamie.dlscorridor.core.constant.MetaConstant;
import cn.jamie.dlscorridor.core.meta.InstanceMeta;
import cn.jamie.dlscorridor.core.provider.ProviderBootstrap;
import cn.jamie.dlscorridor.core.provider.ProviderInvoker;
import cn.jamie.dlscorridor.core.provider.ProviderStorage;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
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
@ConfigurationProperties(prefix = PROVIDER_PREFIX)
@AutoConfigureAfter({DiscorridorAutoConfigure.class, RegistryConfiguration.class})
@Data
public class ProviderAutoConfigure {
    private int port = 8080;
    private boolean gray = false;
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
        InstanceMeta instanceMeta = InstanceMeta.builder().host(ip).port(port).build();
        if (gray) {
            instanceMeta.getParameters().put(MetaConstant.GRAY, "true");
        }
        return instanceMeta;
    }
}
