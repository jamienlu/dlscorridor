package cn.jamie.discorridor.spring.boot.autoconfigure;

import cn.jamie.discorridor.spring.boot.autoconfigure.bean.RegistryConf;
import cn.jamie.dlscorridor.core.registry.RegistryCenter;
import cn.jamie.dlscorridor.core.registry.nacos.NacosRegistryCenter;
import cn.jamie.dlscorridor.core.registry.zookeeper.ZkEnvData;
import cn.jamie.dlscorridor.core.registry.zookeeper.ZkRegistryCenter;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

import static cn.jamie.discorridor.spring.boot.autoconfigure.constant.AutoConfigurationConst.REGISTRY_PREFIX;

/**
 * @author jamieLu
 * @create 2024-03-25
 */
@ConfigurationProperties(prefix = REGISTRY_PREFIX)
@Data
public class RegistryConfiguration {
    @NestedConfigurationProperty
    private RegistryConf meta;

    @Bean(initMethod = "start", destroyMethod = "stop")
    @Order(Integer.MIN_VALUE)
    public RegistryCenter registryCenter() {
        RegistryCenter registryCenter;
        if (meta.getType().equals("zk")) {
            ZkEnvData zkEnvData = ZkEnvData.builder().url(meta.getUrl()).namespace(meta.getNamespace()).baseTime(meta.getOverTime()).maxRetries(meta.getRetryCount())
                .build();
            registryCenter = new ZkRegistryCenter(zkEnvData);
        } else if (meta.getType().equals("nacos")) {
            registryCenter = new NacosRegistryCenter(meta.getUrl());
        } else {
            // 不指定类型使用zk
            ZkEnvData zkEnvData = ZkEnvData.builder().url(meta.getUrl()).namespace(meta.getNamespace()).baseTime(meta.getOverTime()).maxRetries(meta.getRetryCount())
                    .build();
            registryCenter = new ZkRegistryCenter(zkEnvData);
        }
        return registryCenter;
    }
}
