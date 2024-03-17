package cn.jamie.dlscorridor.core.api;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

/**
 * @author jamieLu
 * @create 2024-03-17
 */
public interface LoadBalancer {
    <T> T choose(List<T> providers);
}
