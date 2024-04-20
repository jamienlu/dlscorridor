package io.github.jamienlu.discorridor.core.consumer;

import io.github.jamienlu.discorridor.core.api.RpcContext;
import io.github.jamienlu.discorridor.core.api.RpcInvokeHandler;
import io.github.jamienlu.discorridor.core.api.RpcRequest;
import io.github.jamienlu.discorridor.core.api.RpcResponse;
import io.github.jamienlu.discorridor.core.exception.RpcException;
import io.github.jamienlu.discorridor.core.meta.InstanceMeta;
import io.github.jamienlu.discorridor.core.util.SlidingTimeWindow;
import lombok.extern.slf4j.Slf4j;

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
 * @author jamieLu
 * @create 2024-04-08
 */
@Slf4j
public class StubInvokerHandler implements RpcInvokeHandler {
    private final RpcContext rpcContext;
    private final List<InstanceMeta> instanceMetas;
    private final Set<InstanceMeta> isolatedInstanceMetas = new CopyOnWriteArraySet<>();
    private final List<InstanceMeta> halfOpenInstanceMetas = new CopyOnWriteArrayList<>();
    private final Map<String, SlidingTimeWindow> windows = new ConcurrentHashMap<>();
    private final HandlerParam handlerParam;
    public StubInvokerHandler(RpcContext rpcContext, List<InstanceMeta> instanceMetas) {
        this.rpcContext = rpcContext;
        this.instanceMetas = instanceMetas;
        this.handlerParam = HandlerParam.getInstance(rpcContext.getParameters());
        initHalfThread();
    }
    private void initHalfThread() {
        // 定时线程每隔1分钟读取故障隔离的实例放入待探活集合
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            executor.scheduleWithFixedDelay(this::halfOpen, handlerParam.getHalfOpenInitialDelay(), handlerParam.getHalfOpenDelay(), TimeUnit.SECONDS);
    }
    private void halfOpen() {
        halfOpenInstanceMetas.clear();
        halfOpenInstanceMetas.addAll(isolatedInstanceMetas);
    }


    @Override
    public RpcResponse doInvoke(RpcRequest rpcRequest) {
        RpcResponse rpcResponse = RpcResponse.builder().build();
        InstanceMeta instanceMeta;
        int retry = handlerParam.getRetry();
        while (retry-- > 0) {
            log.debug("retry rpc handler can invoke count:" + retry);
            // 1.rpc实例选择  使用并发集合remove代替锁
            if (!halfOpenInstanceMetas.isEmpty() && (instanceMeta = halfOpenInstanceMetas.remove(0)) != null) {
                // 故障探活
                log.debug("rpc handler type halfOpen");
            } else {
                // 路由负载策略
                log.debug("rpc handler type route balance");
                instanceMeta = rpcContext.getLoadBalancer().choose(rpcContext.getRouter().router(instanceMetas));
            }
            if (instanceMeta == null) {
                rpcResponse.setEx(new RpcException(RpcException.NO_USE_METAINSTANCE));
                continue;
            }
            try {
                // 2.rpc实例调用
                log.info("start real invoke url:" + instanceMeta.toAddress());
                rpcResponse = rpcContext.getTransform().transform(rpcRequest, instanceMeta);
                log.info("end real invoke url:" + instanceMeta.toAddress());
            } catch (Exception e) {
                rpcResponse.setEx(new RpcException(e.getCause(), RpcException.UNKNOWN_EX));
            }
            // 3.rpc故障隔离
            if (!rpcResponse.isStatus()) {
                windows.putIfAbsent(instanceMeta.toPath(), new SlidingTimeWindow(30));
                SlidingTimeWindow window = windows.get(instanceMeta.toPath());
                window.record(System.currentTimeMillis());
                if (window.getSum() > handlerParam.getFaultLimit()) {
                    log.error("instance {} is error, isolatedInstanceMetas={}, instanceMetas={}", instanceMeta, isolatedInstanceMetas, instanceMetas);
                    // retry会失败
                    instanceMetas.remove(instanceMeta);
                    isolatedInstanceMetas.add(instanceMeta);
                }
                continue;
            }
            // 4.rpc 探活恢复  使用并发集合remove代替锁
            // 重写instance equal 避免原引用和故障对象不一致
            if (isolatedInstanceMetas.remove(instanceMeta)) {
                if (!instanceMetas.contains(instanceMeta)) {
                    instanceMetas.add(instanceMeta);
                }
                log.debug("instance {} is recovered, isolatedInstanceMetas={}, instanceMetas={}", instanceMeta, isolatedInstanceMetas, instanceMetas);
            }
            return rpcResponse;
        }
        return rpcResponse;
    }
}
