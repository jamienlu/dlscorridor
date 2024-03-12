package cn.jamie.discorridor.demo.api;

import java.util.Set;

public interface UserService {
    User findById(int id);
    User find(Set<Integer> ids);
    User find(int[] ids);
}
