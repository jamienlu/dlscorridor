package cn.jamie.discorridor.spring.boot.autoconfigure.bean;

import lombok.Data;

/**
 * @author jamieLu
 * @create 2024-03-28
 */
@Data
public class RegistryEnv {
    private String type;
    private String namespace;
    private String url;
    private Integer overTime;
    private Integer retryCount;
    private String username;
    private String password;
}
