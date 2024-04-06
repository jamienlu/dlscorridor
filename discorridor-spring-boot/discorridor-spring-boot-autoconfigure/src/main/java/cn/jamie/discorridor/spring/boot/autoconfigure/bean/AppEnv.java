package cn.jamie.discorridor.spring.boot.autoconfigure.bean;

import lombok.Data;

/**
 * @author jamieLu
 * @create 2024-03-28
 */
@Data
public class AppEnv {
    private String app = "discorridor-app";
    private String namespace = "public";
    private String env = "dev";
    private String name = "discorridor-name";
    private String version = "1.0.0";
    private String group = "DEFAULT_GROUP";
}
