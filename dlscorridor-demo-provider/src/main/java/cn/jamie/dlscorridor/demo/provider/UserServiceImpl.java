package cn.jamie.dlscorridor.demo.provider;

import cn.jamie.discorridor.demo.api.User;
import cn.jamie.discorridor.demo.api.UserService;
import cn.jamie.dlscorridor.core.annotation.JMProvider;
import cn.jamie.dlscorridor.core.annotation.RpcService;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@JMProvider
public class UserServiceImpl implements  @RpcService UserService {
    @Override
    public User findById(int id) {
        return new User(id, "jamie-" + System.currentTimeMillis());
    }

    @Override
    public User find(Set<Integer> ids) {
        return new User(1, "jamie-findSet<Integer>" + System.currentTimeMillis());
    }

    @Override
    public User find(int[] ids) {
        return new User(2, "jamie--findint[]" + System.currentTimeMillis());
    }
}
