package io.github.jamienlu.discorridor.core.api;

import io.github.jamienlu.discorridor.core.exception.RpcException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse {
    // 响应状态
    private boolean status;
    // 数据
    private Object data;
    // 异常响应
    private RpcException ex;
}
