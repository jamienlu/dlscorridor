package cn.jamie.discorridor.spring.boot.autoconfigure.bean;

import lombok.Data;

import static cn.jamie.discorridor.spring.boot.autoconfigure.constant.AutoConfigurationConst.LOADBALANCE_RANDOM;

/**
 * @author jamieLu
 * @create 2024-03-29
 */
@Data
public class CustomerEnv {
    private String filters = "cache";
    private String route;
    private String loadbalance = LOADBALANCE_RANDOM;

    private Integer tokenSize = 2000;
    private Integer tokenRate = 100;

    private Integer timeout = 2000;
    private Integer retry = 3;
    private Integer faultLimit = 5;
    private Integer halfOpenDelay = 60000;
    private Integer halfOpenInitialDelay = 10000;
}
