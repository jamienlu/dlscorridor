package io.github.jamienlu.discorridor.registry.zookeeper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jamieLu
 * @create 2024-03-21
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ZkEnvData {
    private String namespace;
    private String url;
    private Integer baseTime;
    private Integer maxRetries;

}
