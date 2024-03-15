package cn.jamie.dlscorridor.core.conf;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author jamieLu
 * @create 2024-03-15
 */
@Configuration
@PropertySource("classpath:conf.yml")
@Data
public class DisCorridorConf {
    @Value("${serialize.type:fastjson}")
    private String serialize;
}
