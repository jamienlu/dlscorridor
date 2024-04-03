package cn.jamie.discorridor.spring.boot.autoconfigure.bean;

import lombok.Data;

/**
 * @author jamieLu
 * @create 2024-04-04
 */
@Data
public class FilterEnv {
    private String type;

    private Integer tokenSize = 2000;
    private Integer tokenSeconds = 100;

    private Integer cacheSize = 100;
    private Integer cacheSeconds = 60;
}
