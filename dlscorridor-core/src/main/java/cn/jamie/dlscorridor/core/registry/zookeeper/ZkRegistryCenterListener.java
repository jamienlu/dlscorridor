package cn.jamie.dlscorridor.core.registry.zookeeper;

import cn.jamie.dlscorridor.core.meta.InstanceMeta;
import cn.jamie.dlscorridor.core.meta.ServiceMeta;
import cn.jamie.dlscorridor.core.registry.RegistryCenterListener;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 注册中心监听器 监听注册中心事件
 * 每个消费者注册一个监听器  对他归属的服务和实例信息进行操作
 *
 * @author jamieLu
 * @create 2024-03-21
 */
@Slf4j
public class ZkRegistryCenterListener implements RegistryCenterListener {
    private final ZkRegistryEvent zkRegistryEvent;

    public ZkRegistryCenterListener(ZkRegistryEvent zkRegistryEvent) {
        this.zkRegistryEvent = zkRegistryEvent;
    }

    @Override
    public void onRegistry(ServiceMeta serviceMeta) {
        log.debug("watch zkClient registry serviceMeta:" + JSON.toJSONString(serviceMeta));
        zkRegistryEvent.saveServiceMeta(serviceMeta);
    }

    @Override
    public void onUnRegistry(ServiceMeta serviceMeta) {
        log.debug("watch zkClient unregistry serviceMeta:" + JSON.toJSONString(serviceMeta));
        zkRegistryEvent.removeServiceMeta(serviceMeta);
    }

    @Override
    public void onSubscribe(ServiceMeta serviceMeta, List<InstanceMeta> instanceMetas) {
        log.debug("watch zkClient subscribe serviceMeta:" + JSON.toJSONString(serviceMeta));
        zkRegistryEvent.saveServiceInstanceMetas(serviceMeta, instanceMetas);
    }


    @Override
    public void onUnSubscribe(ServiceMeta serviceMeta) {
        log.debug("watch zkClient unsubscribe serviceMeta:" + JSON.toJSONString(serviceMeta));
        zkRegistryEvent.removeServiceInstanceMetas(serviceMeta);
    }
}
