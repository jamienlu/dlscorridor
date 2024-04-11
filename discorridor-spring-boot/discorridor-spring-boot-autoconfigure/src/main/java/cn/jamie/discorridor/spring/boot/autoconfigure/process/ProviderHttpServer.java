package cn.jamie.discorridor.spring.boot.autoconfigure.process;

import cn.jamie.discorridor.spring.boot.autoconfigure.ProviderAutoConfigure;
import cn.jamie.dlscorridor.core.api.RpcRequest;
import cn.jamie.dlscorridor.core.api.RpcResponse;
import cn.jamie.dlscorridor.core.provider.ProviderInvoker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static cn.jamie.discorridor.spring.boot.autoconfigure.constant.AutoConfigurationConst.PROVIDER_PREFIX;

/**
 * @author jamieLu
 * @create 2024-04-02
 */
@Configuration
@ConditionalOnProperty(prefix = PROVIDER_PREFIX, name = "enable")
@AutoConfigureAfter(ProviderAutoConfigure.class)
@RestController
@Slf4j
public class ProviderHttpServer {
    @Autowired
    private ProviderInvoker providerInvoker;
    @PostMapping("/rpc/services")
    public RpcResponse invoke (@RequestBody RpcRequest rpcRequest) {
        return providerInvoker.doInvoke(rpcRequest);
    }
}
