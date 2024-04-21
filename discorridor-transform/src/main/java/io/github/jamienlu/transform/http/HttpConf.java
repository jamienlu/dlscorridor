package io.github.jamienlu.transform.http;

import lombok.Data;

/**
 * @author jamieLu
 * @create 2024-04-12
 */
@Data
public class HttpConf {
    private Integer writeOutTime = 2000;
    private Integer readOutTime = 2000;
    private Integer conOutTime = 5000;
    private Integer maxIdleCons = 16;
    private Integer aliveTime = 60000;
}
