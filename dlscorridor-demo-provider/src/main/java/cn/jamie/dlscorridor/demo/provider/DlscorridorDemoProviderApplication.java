package cn.jamie.dlscorridor.demo.provider;

import cn.jamie.discorridor.spring.boot.autoconfigure.DiscorridorAutoConfigure;
import cn.jamie.discorridor.spring.boot.autoconfigure.ProviderAutoConfigure;
import cn.jamie.discorridor.spring.boot.autoconfigure.process.ProviderHttpServer;
import cn.jamie.dlscorridor.core.meta.ServiceMeta;
import cn.jamie.dlscorridor.core.registry.RegistryCenter;
import com.alibaba.fastjson2.JSON;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableApolloConfig
@RestController
public class DlscorridorDemoProviderApplication {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private DiscorridorAutoConfigure discorridorAutoConfigure;
    @Autowired
    private ServiceMeta serviceMeta;

    public static void main(String[] args) {
        SpringApplication.run(DlscorridorDemoProviderApplication.class, args);
    }
    @GetMapping("/conf")
    public void testConf() {
        System.out.println(JSON.toJSONString(discorridorAutoConfigure));
        System.out.println(JSON.toJSONString(serviceMeta));
    }
    @Bean
    public ApplicationRunner getRunner() {
        return x -> {
            ProviderHttpServer providerController = applicationContext.getBean(ProviderHttpServer.class);
        };
    }

}
