package cn.jamie.dlscorridor.demo.provider;

import cn.jamie.discorridor.demo.api.Order;
import cn.jamie.discorridor.demo.api.OrderService;
import io.github.jamienlu.discorridor.core.annotation.JMProvider;
import io.github.jamienlu.discorridor.core.annotation.RpcService;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;

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
        return Lists.newArrayList(new Order(1, 1000.0D),new Order(2, 2000.0D));
    }

    @Override
    public List<Order> findByIds(Integer[] ids) {

        return Lists.newArrayList(new Order(11, 111.0D),new Order(22, 222.0D));
    }

    @Override
    public List<Order> findByIds(List<Integer> ids) {
        return Lists.newArrayList(new Order(111, 1111.0D),new Order(222, 2222.0D));
    }
}
