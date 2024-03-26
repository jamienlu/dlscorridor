package cn.jamie.discorridor.spring.boot.autoconfigure;

import cn.jamie.dlscorridor.core.api.ServiceEnv;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author jamieLu
 * @create 2024-03-25
 */
@Component
public class DiscorridorAutoConfigure {
    @Value("${discorridor.env}")
    private String env;
    @Value("${discorridor.namespace}")
    private String namespace;
    @Value("${discorridor.app.id}")
    private String app;
    @Value("${discorridor.app.version}")
    private String version;
    @Bean
    public ServiceEnv serviceEnv(){
        return ServiceEnv.builder().app(app).env(env).namespace(namespace).version(version).build();
    };

}
