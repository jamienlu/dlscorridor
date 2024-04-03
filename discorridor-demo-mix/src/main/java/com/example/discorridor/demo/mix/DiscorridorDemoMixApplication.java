package com.example.discorridor.demo.mix;

import cn.jamie.discorridor.demo.api.Order;
import cn.jamie.discorridor.demo.api.OrderService;
import cn.jamie.discorridor.demo.api.User;
import cn.jamie.discorridor.demo.api.UserService;
import cn.jamie.dlscorridor.core.annotation.JMConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class DiscorridorDemoMixApplication {
    @JMConsumer(service = "discorridor-provider", version = "1.0.1")
    UserService userService;
    @JMConsumer(service = "discorridor-provider", version = "1.0.1")
    OrderService orderService;

    public static void main(String[] args) {
        SpringApplication.run(DiscorridorDemoMixApplication.class, args);
    }

    @RequestMapping("/user/id")
    public User findBy(@RequestParam("id") int id) {
        return userService.findById(id);
    }

    @RequestMapping("/order/id")
    public Order findOrderBy(@RequestParam("id") int id) {
        return orderService.findById(id);
    }

}
