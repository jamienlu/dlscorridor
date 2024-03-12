package cn.jamie.dlscorridor.core.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class ConsumerConfig {
    @Bean
    ConsumerBootstrap consumerBootstrap() {
        return new ConsumerBootstrap();
    }
    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner consumerRunnerLoadProxy(@Autowired ConsumerBootstrap consumerBootstrap) {
        return x -> consumerBootstrap.loadConsumerProxy();
    }
}
