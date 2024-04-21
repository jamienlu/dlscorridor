package io.github.jamienlu.discorridor.core.api;

import io.github.jamienlu.discorridor.core.cluster.LoadBalancer;
import io.github.jamienlu.discorridor.core.cluster.Router;
import io.github.jamienlu.discorridor.core.filter.FilterChain;
import io.github.jamienlu.transform.api.RpcTransform;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jamieLu
 * @create 2024-03-17
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcContext {
    // 过滤器链
    private FilterChain filterChain;
    // 路由
    private Router router;
    // 负载均衡器选择
    private LoadBalancer loadBalancer;
    // 参数列表
    private Map<String,String> parameters;
    // 传输协议
    private RpcTransform transform;

    // 线程上下文参数
    public static ThreadLocal<Map<String,String>> contextParameters = ThreadLocal.withInitial(HashMap::new);

    public static void setContextParameter(String key, String value) {
        contextParameters.get().put(key, value);
    }

    public static String getContextParameter(String key) {
        return contextParameters.get().get(key);
    }

    public static void removeContextParameter(String key) {
        contextParameters.get().remove(key);
    }
}
