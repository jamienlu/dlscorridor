package cn.jamie.discorridor.spring.boot.autoconfigure.bean;

import lombok.Data;

/**
 * @author jamieLu
 * @create 2024-03-29
 */
@Data
public class CustomerEnv {
    private String filters;
    private String route;
    private String loadbalance;
}
