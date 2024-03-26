package cn.jamie.dlscorridor.core.api;

import cn.jamie.dlscorridor.core.filter.Filter;
import cn.jamie.dlscorridor.core.filter.FilterChain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author jamieLu
 * @create 2024-03-17
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcContext {
    private FilterChain filterChain;
    private Router router;
    private LoadBalancer loadBalancer;
}
