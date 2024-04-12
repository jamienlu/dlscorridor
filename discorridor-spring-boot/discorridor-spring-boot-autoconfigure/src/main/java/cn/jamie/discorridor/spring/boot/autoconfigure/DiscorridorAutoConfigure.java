package cn.jamie.discorridor.spring.boot.autoconfigure;

import cn.jamie.discorridor.spring.boot.autoconfigure.bean.AppEnv;
import cn.jamie.dlscorridor.core.meta.ServiceMeta;
import cn.jamie.dlscorridor.core.serialization.FastJson2Serializer;
import cn.jamie.dlscorridor.core.serialization.ProtobufSerializer;
import cn.jamie.dlscorridor.core.serialization.SerializationService;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static cn.jamie.discorridor.spring.boot.autoconfigure.constant.AutoConfigurationConst.DISCORRIDOR_PREFIX;

/**
 * @author jamieLu
 * @create 2024-03-25
 */
@Configuration
@ConfigurationProperties(prefix = DISCORRIDOR_PREFIX)
@Data
public class DiscorridorAutoConfigure {
    @NestedConfigurationProperty
    private AppEnv env;
    private String serialization = "fastjson2";
    @Bean
    public ServiceMeta serviceMeta() {
        return ServiceMeta.builder().app(env.getApp()).namespace(env.getNamespace()).env(env.getEnv()).group(env.getGroup()).name(env.getName()).version(env.getVersion())
                .build();
    }
    @Bean
    public SerializationService serializationService() {
        if ("fastjson2".equals(serialization)) {
            return new FastJson2Serializer();
        } else if ("protobuf".equals(serialization)) {
            return new ProtobufSerializer();
        }
        return new FastJson2Serializer();
    }
}
