package com.example.discorridor.demo.mix;

import cn.jamie.discorridor.demo.api.Order;
import cn.jamie.discorridor.demo.api.PayService;
import cn.jamie.discorridor.demo.api.User;
import io.github.jamienlu.discorridor.core.annotation.JMProvider;
import io.github.jamienlu.discorridor.core.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jamieLu
 * @create 2024-04-04
 */
@Slf4j
@JMProvider
public class PayServiceImpl implements @RpcService PayService {
    @Override
    public boolean pay(User user, Order order) {
        log.info("pay:" + user + "##" + order);
        return true;
    }
}
