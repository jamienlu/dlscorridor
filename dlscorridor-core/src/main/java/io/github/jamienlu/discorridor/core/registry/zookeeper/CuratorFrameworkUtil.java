package io.github.jamienlu.discorridor.core.registry.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * CuratorFrameworkUtil zk操作工具
 *
 *
 * @author jamieLu
 * @create 2024-03-22
 */
public class CuratorFrameworkUtil {
    public static CuratorFramework buildCuratorFramework(ZkEnvData zkEnvData) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(zkEnvData.getBaseTime(),zkEnvData.getMaxRetries());
        return CuratorFrameworkFactory.builder()
                .connectString(zkEnvData.getUrl())
                .namespace(zkEnvData.getNamespace())
                .retryPolicy(retryPolicy)
                .build();
    }
}
