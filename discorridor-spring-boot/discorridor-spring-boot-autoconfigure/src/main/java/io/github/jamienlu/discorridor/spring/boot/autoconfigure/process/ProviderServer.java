package io.github.jamienlu.discorridor.spring.boot.autoconfigure.process;

import io.github.jamienlu.discorridor.common.exception.RpcException;
import io.github.jamienlu.discorridor.serialization.api.SerializationService;
import io.github.jamienlu.discorridor.spring.boot.autoconfigure.ProviderAutoConfigure;
import io.github.jamienlu.discorridor.common.api.RpcRequest;
import io.github.jamienlu.discorridor.common.api.RpcResponse;
import io.github.jamienlu.discorridor.core.provider.ProviderInvoker;

import io.github.jamienlu.transform.netty.NettyConf;
import io.github.jamienlu.transform.netty.ProviderNettyServer;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Executors;

import static io.github.jamienlu.discorridor.spring.boot.autoconfigure.constant.AutoConfigurationConst.PROVIDER_PREFIX;

/**
 * @author jamieLu
 * @create 2024-04-02
 */
@Configuration
@ConditionalOnProperty(prefix = PROVIDER_PREFIX, name = "enable")
@AutoConfigureAfter(ProviderAutoConfigure.class)
@RestController
@Slf4j
public class ProviderServer {
    @Autowired
    private NettyConf nettyConf;
    @Autowired
    private ProviderInvoker providerInvoker;
    @Autowired
    private SerializationService serializationService;
    @PostMapping("/rpc/services")
    public RpcResponse invoke (@RequestBody RpcRequest rpcRequest) {
        return providerInvoker.doInvoke(rpcRequest);
    }

    @PostConstruct
    public void nettyServer() {
        if (nettyConf.isEnable()) {
            Executors.newFixedThreadPool(1).submit(() -> {
                try {
                    new ProviderNettyServer(providerInvoker, nettyConf, serializationService).startServer();
                    log.info("mount provider netty server success!");
                } catch (Exception e) {
                    throw new RpcException(e.getCause(),RpcException.NETTY_ERROR);
                }
            });
        }
    }
}
