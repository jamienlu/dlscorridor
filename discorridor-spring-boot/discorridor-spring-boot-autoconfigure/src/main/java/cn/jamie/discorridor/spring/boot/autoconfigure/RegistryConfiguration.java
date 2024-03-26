package cn.jamie.discorridor.spring.boot.autoconfigure;

import cn.jamie.dlscorridor.core.registry.RegistryCenter;
import cn.jamie.dlscorridor.core.registry.RegistryCenterListener;
import cn.jamie.dlscorridor.core.registry.zookeeper.ZkEnvData;
import cn.jamie.dlscorridor.core.registry.zookeeper.ZkRegistryCenterListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static cn.jamie.discorridor.spring.boot.autoconfigure.constant.AutoConfigurationConst.REGISTRY_PREFIX;

/**
 * @author jamieLu
 * @create 2024-03-25
 */
@Configuration
@ConditionalOnProperty(prefix = REGISTRY_PREFIX, name = "enabled", matchIfMissing = true)
public class RegistryConfiguration {
    @Value("${type:zookeeper}")
    private String type;
    @NestedConfigurationProperty
    private ZkEnvData zkEnvData;
    @Bean
    public RegistryCenterListener registryCenterListener() {
        return new ZkRegistryCenterListener();
    }
}
