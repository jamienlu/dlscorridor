package cn.jamie.dlscorridor.core.consumer;

import cn.jamie.dlscorridor.core.api.RpcRequest;
import cn.jamie.dlscorridor.core.api.RpcResponse;
import cn.jamie.dlscorridor.core.util.HttpUtil;
import cn.jamie.dlscorridor.core.util.RpcMethodUtil;
import cn.jamie.dlscorridor.core.util.RpcReflectUtil;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 消费这动态代理调用服务提供者
 */
@Slf4j
public class JMInvocationHandler implements InvocationHandler {
    private Class<?> service;

    public JMInvocationHandler(Class<?> service) {
        this.service = service;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // Object父类方法禁止远程调用
        if (RpcMethodUtil.notPermissionMethod(method.getName())) {
            return null;
        }
        RpcRequest rpcRequest = RpcRequest.builder()
                .service(service.getCanonicalName())
                .methodSign(RpcReflectUtil.analysisMethodSign(method))
                .args(args).build();
        RpcResponse rpcResponse = post(rpcRequest);
        if (rpcResponse.isStatus()) {
            return JSON.to(method.getReturnType(), rpcResponse.getData());
        } else {
            Exception exception = rpcResponse.getEx();
            log.error(exception.getMessage(),exception);
            throw exception;
        }
    }
    //
    private RpcResponse post(RpcRequest rpcRequest) {
        String res = null;
        try {
            res = HttpUtil.postOkHttp("http://localhost:8080/", JSON.toJSONString(rpcRequest));
            return JSON.parseObject(res, RpcResponse.class);
        } catch (IOException e) {
            return RpcResponse.builder().status(false).ex(e).build();
        }
    }
}
