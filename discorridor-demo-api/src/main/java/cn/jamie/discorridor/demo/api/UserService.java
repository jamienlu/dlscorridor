package cn.jamie.discorridor.demo.api;

import java.util.List;
import java.util.Set;

public interface UserService {
    User findById(long id);
    long find(Set<Integer> ids);
    List<User> search(User user);

    User findTimeout(long ids);
}
