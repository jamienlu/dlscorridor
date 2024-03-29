package cn.jamie.discorridor.spring.boot.autoconfigure;

import cn.jamie.discorridor.spring.boot.autoconfigure.bean.RegistryEnv;
import cn.jamie.dlscorridor.core.registry.RegistryCenter;
import cn.jamie.dlscorridor.core.registry.RegistryStorage;
import cn.jamie.dlscorridor.core.registry.zookeeper.ZkEnvData;
import cn.jamie.dlscorridor.core.registry.zookeeper.ZkRegistryCenter;
import cn.jamie.dlscorridor.core.registry.zookeeper.ZkRegistryStorage;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Bean;

import static cn.jamie.discorridor.spring.boot.autoconfigure.constant.AutoConfigurationConst.REGISTRY_PREFIX;

/**
 * @author jamieLu
 * @create 2024-03-25
 */
@ConfigurationProperties(prefix = REGISTRY_PREFIX)
@Data
public class RegistryConfiguration {
    @NestedConfigurationProperty
    private RegistryEnv env;

    @Bean
    public RegistryStorage registryStorage() {
        return new ZkRegistryStorage();
    }
    @Bean(initMethod = "start", destroyMethod = "stop")
    public RegistryCenter registryCenter(@Autowired RegistryStorage registryStorage) {
        ZkEnvData zkEnvData = ZkEnvData.builder().url(env.getUrl()).namespace(env.getNamespace()).baseTime(env.getOverTime()).maxRetries(env.getRetryCount())
                .build();
        return new ZkRegistryCenter(zkEnvData,registryStorage);
    }
}
