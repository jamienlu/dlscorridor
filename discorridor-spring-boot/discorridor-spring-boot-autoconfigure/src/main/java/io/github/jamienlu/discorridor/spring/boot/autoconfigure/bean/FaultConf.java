package io.github.jamienlu.discorridor.spring.boot.autoconfigure.bean;

import lombok.Data;

/**
 * @author jamieLu
 * @create 2024-04-04
 */
@Data
public class FaultConf {
    private Integer timeout = 2000;
    private Integer retry = 3;
    private Integer faultLimit = 5;
    private Integer halfOpenDelay = 60;
    private Integer halfOpenInitialDelay = 10;
}
