package cn.jamie.dlscorridor.core.consumer;

import cn.jamie.dlscorridor.core.api.LoadBalancer;
import cn.jamie.dlscorridor.core.api.Router;
import cn.jamie.dlscorridor.core.api.RpcContext;
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
import java.util.List;

/**
 * 消费这动态代理调用服务提供者
 */
@Slf4j
public class JMInvocationHandler implements InvocationHandler {
    private Class<?> service;
    private RpcContext rpcContext;
    private List<String> urls;

    public JMInvocationHandler(Class<?> service, RpcContext rpcContext, List<String> urls) {
        this.service = service;
        this.rpcContext = rpcContext;
        this.urls = urls;
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
        String url = rpcContext.getLoadBalancer().choose(rpcContext.getRouter().router(urls));
        log.info("real invoke url:" + url);
        RpcResponse rpcResponse = post(rpcRequest,url);
        if (rpcResponse.isStatus()) {
            return JSON.to(method.getReturnType(), rpcResponse.getData());
        } else {
            Exception exception = rpcResponse.getEx();
            log.error(exception.getMessage(),exception);
            throw exception;
        }
    }
    //
    private RpcResponse post(RpcRequest rpcRequest, String url) {
        String res = null;
        try {
            res = HttpUtil.postOkHttp(url, JSON.toJSONString(rpcRequest));
            return JSON.parseObject(res, RpcResponse.class);
        } catch (IOException e) {
            return RpcResponse.builder().status(false).ex(e).build();
        }
    }
}
