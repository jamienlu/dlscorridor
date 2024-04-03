package cn.jamie.dlscorridor.core.api;

import cn.jamie.dlscorridor.core.cluster.LoadBalancer;
import cn.jamie.dlscorridor.core.cluster.Router;
import cn.jamie.dlscorridor.core.filter.FilterChain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
