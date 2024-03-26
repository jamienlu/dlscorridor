package cn.jamie.dlscorridor.core.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jamieLu
 * @create 2024-03-26
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceEnv {
    private String env;
    private String namespace;
    private String app;
    private String version;
}
