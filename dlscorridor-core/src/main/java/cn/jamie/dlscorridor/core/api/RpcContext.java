package cn.jamie.dlscorridor.core.api;

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
    private List<Filter> filters;
    private Router router;
    private LoadBalancer loadBalancer;
}
