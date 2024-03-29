package cn.jamie.dlscorridor.demo.provider;

import cn.jamie.discorridor.demo.api.Order;
import cn.jamie.discorridor.demo.api.OrderService;
import cn.jamie.dlscorridor.core.annotation.JMProvider;
import cn.jamie.dlscorridor.core.annotation.RpcService;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

@JMProvider
public class OrderServiceImpl implements @RpcService OrderService, Serializable {
    @Override
    public Order findById(int id) {
        if (id == 404) {
            throw new RuntimeException("404 Order Exception");
        }
        return new Order(id, 1000.0D);
    }

    @Override
    public List<Order> findByIds(int[] ids) {

        return List.of(new Order(1, 1000.0D),new Order(2, 2000.0D));
    }

    @Override
    public List<Order> findByIds(Integer[] ids) {
        return List.of(new Order(11, 1000.0D),new Order(22, 2200.0D));
    }

    @Override
    public List<Order> findByIds(List<Integer> ids) {
        return List.of(new Order(111, 1000.0D),new Order(222, 2220.0D));
    }
}
