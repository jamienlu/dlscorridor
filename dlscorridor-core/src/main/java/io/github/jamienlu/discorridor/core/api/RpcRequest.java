package io.github.jamienlu.discorridor.core.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest {
    // 请求接口
    private String service;
    // 请求方法名
    private String methodSign;
    // 请求参数
    private Object[] args;
    // 透传参数给RPC环境上下文
    @Builder.Default
    private Map<String,String> parameters = new HashMap<>();
}
