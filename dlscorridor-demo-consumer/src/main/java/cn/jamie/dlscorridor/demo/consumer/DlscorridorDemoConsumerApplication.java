package cn.jamie.dlscorridor.demo.consumer;

import cn.jamie.discorridor.demo.api.Order;
import cn.jamie.discorridor.demo.api.OrderService;
import cn.jamie.discorridor.demo.api.User;
import cn.jamie.discorridor.demo.api.UserService;
import cn.jamie.dlscorridor.core.annotation.JMConsumer;
import cn.jamie.dlscorridor.core.conf.DisCorridorConf;
import cn.jamie.dlscorridor.core.consumer.ConsumerConfig;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@SpringBootApplication
@RestController
@Import({ConsumerConfig.class, DisCorridorConf.class})
public class DlscorridorDemoConsumerApplication {
	@JMConsumer
	UserService userService;
	@JMConsumer
	OrderService orderService;

	public static void main(String[] args) {
		SpringApplication.run(DlscorridorDemoConsumerApplication.class, args);
	}
	@RequestMapping("/")
	public User findBy(@RequestParam("id") int id) {
		return userService.findById(id);
	}

	@RequestMapping("/order")
	public Order findOrderBy(@RequestParam("id") int id) {
		return orderService.findById(id);
	}

	@Bean
	public ApplicationRunner consumerRunner() {
		return x -> {
			User user = userService.findById(1L);
			System.out.println(user);
			Order order = orderService.findById(1);
			System.out.println(order);
			List<Order> tt = orderService.findByIds(new int[]{1,2});
			System.out.println(tt);
			List<Order> orders = orderService.findByIds(List.of(1,2));
			System.out.println(orders);
			System.out.println(orderService.findByIds(new Integer[]{1,2}));;
			/*System.out.println(orderService.findById(404));*/
			System.out.println(userService.findById(1L));
			System.out.println(userService.search(user));
			System.out.println(userService.find(null));
		};
	}
}
