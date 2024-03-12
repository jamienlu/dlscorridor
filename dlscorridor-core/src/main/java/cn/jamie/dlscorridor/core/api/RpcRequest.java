package cn.jamie.dlscorridor.core.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest {
    // 请求接口
    private String service;
    // 请求方法名
    private String methodName;
    // 与请求参数一一对应
    private List<Class<?>> methodParaTypes;
    // 请求参数
    private Object[] args;
}
