package cn.jamie.discorridor.spring.boot.autoconfigure.bean;

import lombok.Data;

import static cn.jamie.discorridor.spring.boot.autoconfigure.constant.AutoConfigurationConst.LOADBALANCE_RANDOM;

/**
 * @author jamieLu
 * @create 2024-04-04
 */
@Data
public class LoadBalanceEnv {
    private String type = LOADBALANCE_RANDOM;
}
