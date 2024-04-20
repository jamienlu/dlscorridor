package io.github.jamienlu.discorridor.core.meta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

/**
 * 服务提供者元数据 映射关系
 *
 * @author jamieLu
 * @create 2024-03-13
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProviderMeta {
    // 方法签名
    private String methodSign;
    // 服务方法
    private Method method;
    // 服务实现类
    private Object serviceImpl;
}
