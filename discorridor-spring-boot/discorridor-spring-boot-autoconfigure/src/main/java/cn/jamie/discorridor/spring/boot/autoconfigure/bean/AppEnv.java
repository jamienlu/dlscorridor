package cn.jamie.discorridor.spring.boot.autoconfigure.bean;

import lombok.Data;

/**
 * @author jamieLu
 * @create 2024-03-28
 */
@Data
public class AppEnv {
    private String app;
    private String namespace;
    private String env;
    private String name;
    private String version;
}
