package io.github.jamienlu.discorridor.spring.boot.autoconfigure.bean;

import lombok.Data;

import static io.github.jamienlu.discorridor.spring.boot.autoconfigure.constant.AutoConfigurationConst.LOADBALANCE_RANDOM;

/**
 * @author jamieLu
 * @create 2024-04-04
 */
@Data
public class LoadBalanceConf {
    private String type = LOADBALANCE_RANDOM;
}
