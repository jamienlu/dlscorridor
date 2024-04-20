package io.github.jamienlu.discorridor.spring.boot.autoconfigure.bean;

import lombok.Data;

/**
 * @author jamieLu
 * @create 2024-04-04
 */
@Data
public class GrayConf {
    private Boolean enable = false;
    private Integer ratio = 50;
}
