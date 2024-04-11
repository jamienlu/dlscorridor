package cn.jamie.dlscorridor.demo.provider;

import cn.jamie.discorridor.spring.boot.autoconfigure.process.ProviderHttpServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DlscorridorDemoProviderApplication {
    @Autowired
    private ApplicationContext applicationContext;
    public static void main(String[] args) {
        SpringApplication.run(DlscorridorDemoProviderApplication.class, args);
    }

    @Bean
    public ApplicationRunner getRunner() {
        return x -> {
            ProviderHttpServer providerController = applicationContext.getBean(ProviderHttpServer.class);
        };
    }

}
