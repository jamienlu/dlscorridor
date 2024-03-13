package cn.jamie.dlscorridor.demo.provider;

import cn.jamie.dlscorridor.core.api.RpcRequest;
import cn.jamie.dlscorridor.core.api.RpcResponse;
import cn.jamie.dlscorridor.core.provider.ProviderBootstrap;
import cn.jamie.dlscorridor.core.provider.ProviderConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@Import(ProviderConfig.class)
public class DlscorridorDemoProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(DlscorridorDemoProviderApplication.class, args);
    }

    // 使用spring web的http通信 json序列化
    @RequestMapping("/")
    public RpcResponse invoke (@RequestBody RpcRequest rpcRequest) {

        return providerBootstrap.invoke(rpcRequest);
    }
    @Autowired
    private ProviderBootstrap providerBootstrap;

    @Bean
    public ApplicationRunner getRunner() {
        return x -> {
            RpcRequest rpcRequest = RpcRequest.builder()
                .service("cn.jamie.discorridor.demo.api.UserService")
                .methodSign("findById/long").args(new Object[]{100}).build();
            RpcResponse<?> res = invoke(rpcRequest);
            System.out.println("return res:" + res.getData());

        };
    }

}
