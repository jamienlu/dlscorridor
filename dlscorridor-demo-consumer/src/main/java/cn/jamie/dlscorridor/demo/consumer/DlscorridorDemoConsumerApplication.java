package cn.jamie.dlscorridor.demo.consumer;

import cn.jamie.discorridor.demo.api.Order;
import cn.jamie.discorridor.demo.api.OrderService;
import cn.jamie.discorridor.demo.api.PayService;
import cn.jamie.discorridor.demo.api.User;
import cn.jamie.discorridor.demo.api.UserService;
import cn.jamie.dlscorridor.core.annotation.JMConsumer;
import cn.jamie.dlscorridor.core.api.RpcContext;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SpringBootApplication
@RestController
public class DlscorridorDemoConsumerApplication {
	@JMConsumer(service = "discorridor-provider", version = "1.0.1")
	UserService userService;
	@JMConsumer(service = "discorridor-provider", version = "1.0.1")
	OrderService orderService;
	@JMConsumer(service = "discorridor-pay", version = "1.0.0")
	PayService payService;

	public static void main(String[] args) {
		SpringApplication.run(DlscorridorDemoConsumerApplication.class, args);
	}
	@RequestMapping("/user/get")
	public User findBy(@RequestParam("id") int id) {
		return userService.findById(id);
	}

	@GetMapping("/pay")
	public Boolean findBy(@RequestParam("userId") int userId, @RequestParam("orderId") int orderId) {
		return payService.pay(userService.findById(userId),orderService.findById(orderId));
	}
	@GetMapping("/user/out")
	public User findTimeout(@RequestParam("id") long id) {
		return userService.findTimeout(id);
	}

	@Bean
	public ApplicationRunner consumerRunner() {
		return x -> {
			User user = userService.findById(1L);
			System.out.println(user);
/*
			RpcContext.setContextParameter("time", String.valueOf(System.currentTimeMillis()));
*/
			System.out.println(userService.context("time"));
			Order order = orderService.findById(1);
			System.out.println(userService.context("time"));
			System.out.println(order);
			List<Order> tt = orderService.findByIds(new int[]{1,2});
			System.out.println(tt);
			List<Order> orders = orderService.findByIds(Lists.newArrayList(1,2));
			System.out.println(orders);
			System.out.println(orderService.findByIds(new Integer[]{1,2}));;
			/*System.out.println(orderService.findById(404));*/

			System.out.println(userService.search(user));
			System.out.println(userService.find(null));
		};
	}
}
