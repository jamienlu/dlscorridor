package cn.jamie.dlscorridor.core.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse<T> {
    // 响应状态
    private boolean status;
    // 数据
    private T data;
    // 异常响应
    private Exception ex;
}
