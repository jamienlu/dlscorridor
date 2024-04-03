package cn.jamie.dlscorridor.demo.provider;

import cn.jamie.discorridor.spring.boot.autoconfigure.process.ProviderController;
import cn.jamie.dlscorridor.core.api.RpcRequest;
import cn.jamie.dlscorridor.core.api.RpcResponse;
import cn.jamie.dlscorridor.core.meta.InstanceMeta;
import cn.jamie.dlscorridor.core.meta.ServiceMeta;
import cn.jamie.dlscorridor.core.provider.ProviderInvoker;
import cn.jamie.dlscorridor.core.provider.ProviderStorage;
import cn.jamie.dlscorridor.core.registry.RegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            ProviderController providerController = applicationContext.getBean(ProviderController.class);
            System.out.println(1);
        };
    }

}
