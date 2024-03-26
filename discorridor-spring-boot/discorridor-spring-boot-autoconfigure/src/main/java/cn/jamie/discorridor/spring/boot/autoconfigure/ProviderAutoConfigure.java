package cn.jamie.discorridor.spring.boot.autoconfigure;

import cn.jamie.dlscorridor.core.meta.ServiceMeta;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

import static cn.jamie.discorridor.spring.boot.autoconfigure.constant.AutoConfigurationConst.PROVIDER_PREFIX;

/**
 * @author jamieLu
 * @create 2024-03-25
 */
@Configuration
@ConditionalOnProperty(prefix = PROVIDER_PREFIX, name = "enabled")
@AutoConfigureAfter(DiscorridorAutoConfigure.class)
public class ProviderAutoConfigure {
    @NestedConfigurationProperty
    private ServiceMeta serviceMeta;
}
