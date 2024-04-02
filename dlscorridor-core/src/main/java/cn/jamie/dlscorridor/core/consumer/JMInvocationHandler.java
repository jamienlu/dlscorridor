package cn.jamie.dlscorridor.core.consumer;

import cn.jamie.dlscorridor.core.api.RpcContext;
import cn.jamie.dlscorridor.core.api.RpcRequest;
import cn.jamie.dlscorridor.core.api.RpcResponse;
import cn.jamie.dlscorridor.core.exception.RpcException;
import cn.jamie.dlscorridor.core.meta.InstanceMeta;
import cn.jamie.dlscorridor.core.transform.HttpInvoker;
import cn.jamie.dlscorridor.core.util.HttpUtil;
import cn.jamie.dlscorridor.core.util.RpcMethodUtil;
import cn.jamie.dlscorridor.core.util.RpcReflectUtil;

import cn.jamie.dlscorridor.core.util.SlidingTimeWindow;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 消费这动态代理调用服务提供者
 */
@Slf4j
public class JMInvocationHandler implements InvocationHandler {
    private final Class<?> service;
    private final RpcContext rpcContext;
    private final List<InstanceMeta> instanceMetas;
    private final Set<InstanceMeta> isolatedInstanceMetas = new CopyOnWriteArraySet<>();
    private final List<InstanceMeta> halfOpenInstanceMetas = new CopyOnWriteArrayList<>();
    final Map<String, SlidingTimeWindow> windows = new ConcurrentHashMap<>();

    public JMInvocationHandler(Class<?> service, RpcContext rpcContext, List<InstanceMeta> instanceMetas) {
        this.service = service;
        this.rpcContext = rpcContext;
        this.instanceMetas = instanceMetas;
        // 定时线程每隔1分钟读取故障隔离的实例放入待探活集合
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleWithFixedDelay(this::halfOpen, 10, 60, TimeUnit.SECONDS);
    }
    private void halfOpen() {
        halfOpenInstanceMetas.clear();
        halfOpenInstanceMetas.addAll(isolatedInstanceMetas);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // Object父类方法禁止远程调用
        if (RpcMethodUtil.notPermissionMethod(method.getName())) {
            return null;
        }
        // 组装远程调用参数
        RpcRequest rpcRequest = RpcRequest.builder()
            .service(service.getCanonicalName())
            .methodSign(RpcReflectUtil.analysisMethodSign(method))
            .args(args).build();
        RpcResponse rpcResponse = RpcResponse.builder().status(false).data(null).build();
        rpcContext.getFilterChain().doFilter(rpcRequest, rpcResponse, this::handler);
        if (rpcResponse.isStatus()) {
            return JSON.to(method.getReturnType(), rpcResponse.getData());
        } else {
            Exception exception = rpcResponse.getEx();
            if(exception instanceof RpcException ex) {
                throw ex;
            } else {
                throw new RpcException(exception, RpcException.UnknownEx);
            }
        }
    }
    private RpcResponse handler(RpcRequest rpcRequest) {
        RpcResponse rpcResponse = RpcResponse.builder().status(false).build();;
        InstanceMeta instanceMeta = null;
        int retry = Integer.parseInt(rpcContext.getParameters()
            .getOrDefault("app.retry", "3"));
        int faultLimit = Integer.parseInt(rpcContext.getParameters()
            .getOrDefault("app.faultLimit", "5"));
        while (retry-- > 0) {
            log.debug("retry rpc handler can invoke count:" + retry);
            // 1.rpc实例获取
            try {
                if (!halfOpenInstanceMetas.isEmpty()) {
                    // 故障探活
                    instanceMeta = halfOpenInstanceMetas.remove(0);
                } else {
                    // 远程调用路由
                    instanceMeta = rpcContext.getLoadBalancer().choose(rpcContext.getRouter().router(instanceMetas));
                }
                if (instanceMeta == null) {
                    continue;
                }
                // 2.rpc实例调用
                log.info("real invoke url:" + instanceMeta.toAddress());
                rpcResponse =  post(rpcRequest,HttpUtil.convertIpAddressToHttp(instanceMeta.toAddress()));
            } catch (Exception e) {
                // 故障隔离
                log.error("rpc handle is error", e);
                if (null != instanceMeta) {
                    windows.putIfAbsent(instanceMeta.toPath(), new SlidingTimeWindow(30));
                    SlidingTimeWindow window = windows.get(instanceMeta.toPath());
                    window.record(System.currentTimeMillis());
                    if (window.getSum() > faultLimit) {
                        log.error("instance {} is error, isolatedInstanceMetas={}, instanceMetas={}", instanceMeta, isolatedInstanceMetas, instanceMetas);
                        instanceMetas.remove(instanceMeta);
                        isolatedInstanceMetas.add(instanceMeta);
                    }
                }
                throw e;
            }
            // 3.rpc 探活恢复
            synchronized (instanceMetas) {
                if (!instanceMetas.contains(instanceMeta)) {
                    isolatedInstanceMetas.remove(instanceMeta);
                    instanceMetas.add(instanceMeta);
                    log.debug("instance {} is recovered, isolatedInstanceMetas={}, instanceMetas={}", instanceMeta, isolatedInstanceMetas, instanceMetas);
                }
            }
            return rpcResponse;
        }
        rpcResponse.setEx(new RpcException(RpcException.NO_USE_METAINSTANCE));
        return rpcResponse;
    }

    private RpcResponse post(RpcRequest rpcRequest, String url) {
        String res;
        try {
            res = HttpInvoker.postOkHttp(url, JSON.toJSONString(rpcRequest));
            return JSON.parseObject(res, RpcResponse.class);
        } catch (IOException e) {
            return RpcResponse.builder().status(false).ex(e).build();
        }
    }
}
