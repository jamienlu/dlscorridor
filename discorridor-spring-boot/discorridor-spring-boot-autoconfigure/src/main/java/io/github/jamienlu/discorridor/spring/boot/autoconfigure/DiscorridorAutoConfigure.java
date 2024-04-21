package io.github.jamienlu.discorridor.spring.boot.autoconfigure;

import io.github.jamienlu.discorridor.spring.boot.autoconfigure.bean.AppConf;
import io.github.jamienlu.discorridor.spring.boot.autoconfigure.process.ApolloConfigChanged;
import io.github.jamienlu.discorridor.common.constant.MetaConstant;
import io.github.jamienlu.discorridor.common.meta.ServiceMeta;
import io.github.jamienlu.discorridor.serialization.api.SerializationService;
import io.github.jamienlu.discorridor.serialization.service.ProtobufSerializer;
import io.github.jamienlu.discorridor.serialization.service.FastJson2Serializer;
import io.github.jamienlu.transform.http.HttpConf;
import io.github.jamienlu.transform.netty.NettyConf;
import io.github.jamienlu.discorridor.spring.boot.autoconfigure.constant.AutoConfigurationConst;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author jamieLu
 * @create 2024-03-25
 */
@Configuration
@ConfigurationProperties(prefix = AutoConfigurationConst.DISCORRIDOR_PREFIX)
@Data
public class DiscorridorAutoConfigure {
    @NestedConfigurationProperty
    private AppConf meta = new AppConf();
    @NestedConfigurationProperty
    private NettyConf netty = new NettyConf();
    @NestedConfigurationProperty
    private HttpConf http = new HttpConf();
    private String app = "discorridor-app";
    private String name = "discorridor-name";
    private String version = "1.0.0";
    @Bean
    @ConditionalOnMissingBean
    public ApolloConfigChanged apolloConfigChanged() {
        return new ApolloConfigChanged();
    }
    @Bean
    @ConditionalOnMissingBean
    public NettyConf netty() {
        return netty;
    }
    @Bean
    @ConditionalOnMissingBean
    public HttpConf http() {
        return http;
    }
    @Bean
    @RefreshScope
    public ServiceMeta serviceMeta() {
        ServiceMeta serviceMeta =  ServiceMeta.builder().app(app).namespace(meta.getNamespace()).env(meta.getEnv()).group(meta.getGroup()).name(name).version(version)
            .build();
        serviceMeta.addMeta("dc", String.valueOf(meta.getDc()));
        serviceMeta.addMeta("tc", String.valueOf(meta.getTc()));
        serviceMeta.addMeta("unit", String.valueOf(meta.getUnit()));
        serviceMeta.addMeta("gray", String.valueOf(meta.isGray()));
        return serviceMeta;
    }
    @Bean
    public SerializationService serializationService() {
        if (MetaConstant.SERIALIZATION_FASTJSON2.equals(meta.getSerialization())) {
            return new FastJson2Serializer();
        } else if (MetaConstant.SERIALIZATION_PROTOBUF.equals(meta.getSerialization())) {
            return new ProtobufSerializer();
        }
        return new FastJson2Serializer();
    }
}
