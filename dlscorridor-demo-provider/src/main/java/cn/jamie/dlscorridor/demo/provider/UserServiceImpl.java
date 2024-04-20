package cn.jamie.dlscorridor.demo.provider;

import cn.jamie.discorridor.demo.api.User;
import cn.jamie.discorridor.demo.api.UserService;
import io.github.jamienlu.discorridor.core.annotation.JMProvider;
import io.github.jamienlu.discorridor.core.annotation.RpcService;
import io.github.jamienlu.discorridor.core.api.RpcContext;
import io.github.jamienlu.discorridor.core.exception.RpcException;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;

import java.util.List;
import java.util.Set;

@JMProvider
public class UserServiceImpl implements  @RpcService UserService {

    @Override
    public User findById(long id) {
        return new User(22, "|jamie-" + System.currentTimeMillis());
    }

    @Override
    public long find(Set<Integer> ids) {
        return 99l;/*new User(1, "jamie-findSet<Integer>" + System.currentTimeMillis());*/
    }

    @Override
    public List<User> search(User user) {
        return Lists.newArrayList(user);
    }

    @Override
    public User findTimeout(long ids) {
        if (ids > 100) {
            throw new RpcException(RpcException.SOCKET_TIMEOUT_EX);
        }
        return new User(1, "|jamie-" + System.currentTimeMillis());
    }

    @Override
    public String context(String key) {
        return RpcContext.getContextParameter(key);
    }
}
