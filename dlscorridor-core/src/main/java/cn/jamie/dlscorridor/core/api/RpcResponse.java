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
    private boolean status;
    private T data;
    private Exception ex;
}
