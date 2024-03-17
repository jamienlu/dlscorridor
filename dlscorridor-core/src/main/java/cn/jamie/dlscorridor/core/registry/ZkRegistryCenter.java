package cn.jamie.dlscorridor.core.registry;

import cn.jamie.dlscorridor.core.api.RegistryCenter;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.BoundedExponentialBackoffRetry;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;

/**
 * @author jamieLu
 * @create 2024-03-17
 */
public class ZkRegistryCenter implements RegistryCenter {
    private CuratorFramework client;
    @Override
    public void start() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
        client = CuratorFrameworkFactory.builder()
                .connectString("192.168.0.101:2181")
                .namespace("discorridor")
                .retryPolicy(retryPolicy)
                .build();
        client.start();
    }

    @Override
    public void stop() {
        client.close();
    }

    @Override
    public void register(String service, String instance) {
        String serverPath = "/" + service;
        try {
            // 创建服务的持久化节点
            if(client.checkExists().forPath(serverPath) == null) {
                client.create().withMode(CreateMode.PERSISTENT).forPath(serverPath,"service".getBytes());
            }
            // 创建实例的临时节点
            String instancePath = serverPath + "/" + instance;
            client.create().withMode(CreateMode.EPHEMERAL).forPath(instancePath,"provider".getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unregister(String service, String instance) {
        String serverPath = "/" + service;
        try {
            // 删除服务的持久化节点
            if(client.checkExists().forPath(serverPath) == null) {
                return;
            }
            // 删除实例的临时节点
            String instancePath = serverPath + "/" + instance;
            client.delete().quietly().forPath(instancePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> fectchAll(String service) {
        return null;
    }

    @Override
    public void subscribe() {

    }
}
