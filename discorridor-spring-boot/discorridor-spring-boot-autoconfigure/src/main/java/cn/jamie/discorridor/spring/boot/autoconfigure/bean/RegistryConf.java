package cn.jamie.discorridor.spring.boot.autoconfigure.bean;

import lombok.Data;

/**
 * @author jamieLu
 * @create 2024-03-28
 */
@Data
public class RegistryConf {
    private String type = "zk";
    private String namespace = "public";
    private String url = "192.168.0.102:2181";
    private Integer overTime = 1000;
    private Integer retryCount = 3;
    private String username = "root";
    private String password = "123456";
}
