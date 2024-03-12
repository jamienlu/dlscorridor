package cn.jamie.discorridor.demo.api;

import java.util.List;

public interface OrderService {
    Order findById(int id);
    List<Order> findByIds(int[] ids);
    List<Order> findByIds(Integer[] ids);
    List<Order> findByIds(List<Integer> ids);
}
