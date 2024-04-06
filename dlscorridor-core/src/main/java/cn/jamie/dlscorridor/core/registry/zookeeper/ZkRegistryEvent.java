package cn.jamie.dlscorridor.core.registry.zookeeper;

import cn.jamie.dlscorridor.core.registry.AbstractRegistryEvent;
import cn.jamie.dlscorridor.core.registry.RegistryEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * 目前设计是没有一个服务或消费实例就会持有一个各自的storage
 *
 * @author jamieLu
 * @create 2024-03-29
 */
@Slf4j
public class ZkRegistryEvent extends AbstractRegistryEvent implements RegistryEvent {
}
