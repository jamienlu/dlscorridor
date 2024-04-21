package io.github.jamienlu.discorridor.core.registry.nacos;

import io.github.jamienlu.discorridor.common.constant.MetaConstant;
import io.github.jamienlu.discorridor.common.exception.RpcException;
import io.github.jamienlu.discorridor.common.meta.InstanceMeta;
import io.github.jamienlu.discorridor.common.meta.ServiceMeta;
import io.github.jamienlu.discorridor.core.registry.RegistryCenter;
import io.github.jamienlu.discorridor.core.registry.RegistryCenterListener;
import com.alibaba.fastjson2.JSON;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.Service;
import com.alibaba.nacos.common.utils.CollectionUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author jamieLu
 * @create 2024-04-05
 */
@Slf4j
public class NacosRegistryCenter implements RegistryCenter {
    private final String serverAddress;
    private NamingService namingService;
    private final NacosRegistryEvent nacosRegistryEvent = new NacosRegistryEvent();
    private final RegistryCenterListener registryCenterListener;
    public NacosRegistryCenter(String serverAddress) {
        this.serverAddress = serverAddress;
        registryCenterListener = new NacosRegistryListener(nacosRegistryEvent);
    }
    private void listenerEvent(Consumer<RegistryCenterListener> consumer) {
        consumer.accept(registryCenterListener);
    }

    @Override
    public void start() {
        try {
            namingService = NamingFactory.createNamingService(serverAddress);
        } catch (NacosException e) {
            log.error("start nacos server error", e);
            throw new RpcException(RpcException.NO_NACOS_SERVER);
        }
        log.info("nacos center started");
    }

    @Override
    public void stop() {
        try {
            namingService.shutDown();
        } catch (NacosException e) {
            log.error("stop nacos server error", e);
            throw new RpcException(e, RpcException.NO_NACOS_SERVER);
        }
        log.info("nacos center stopped");
    }

    @Override
    public void register(ServiceMeta service, InstanceMeta instance) {
        log.debug("nacos center register service:" + JSON.toJSONString(service));
        log.debug("nacos center register instance:" + JSON.toJSONString(instance));
        Service nacosServer = NacosServerInstanceUtil.createNacosServer(service);
        Instance nacosInstance = NacosServerInstanceUtil.createNacosInstance(nacosServer.getName(),instance);
        // 服务元数据挂载到实例上
        nacosInstance.getMetadata().putAll(service.getParameters());
        String clusterName = nacosInstance.getMetadata().get("dc");
        try {
            if (null != clusterName) {
                nacosInstance.setClusterName(clusterName);
            }
            namingService.registerInstance(nacosServer.getName(), nacosServer.getGroupName(), nacosInstance);
        } catch (NacosException e) {
            log.error("register nacos instance error", e);
            throw new RpcException(e, RpcException.NO_NACOS_INSTANCE);
        } finally {
            listenerEvent((listener -> listener.onRegistry(service)));
        }
    }

    @Override
    public void unregister(ServiceMeta service, InstanceMeta instance) {
        log.debug("nacos center unregister service:" + JSON.toJSONString(service));
        log.debug("nacos center unregister instance:" + JSON.toJSONString(instance));
        Service nacosServer = NacosServerInstanceUtil.createNacosServer(service);
        Instance nacosInstance = NacosServerInstanceUtil.createNacosInstance(nacosServer.getName(),instance);
        String clusterName = nacosInstance.getMetadata().get("dc");
        try {
            if (null != clusterName) {
                nacosInstance.setClusterName(clusterName);
            }
            namingService.deregisterInstance(nacosServer.getName(), nacosServer.getGroupName(), nacosInstance);
        } catch (NacosException e) {
            log.error("unregister nacos instance error", e);
            throw new RpcException(e, RpcException.NO_NACOS_INSTANCE);
        } finally {
            listenerEvent((listener -> listener.onUnRegistry(service)));
        }
    }

    @Override
    public List<InstanceMeta> fectchAll(ServiceMeta service) {
        // 自存储后减少一次和nacos的通讯
        return nacosRegistryEvent.searchInstanceMetas(service);
    }
    private List<InstanceMeta> fetchAllNacos(ServiceMeta service) {
        try {
            List<Instance> instances;
            if (service.getParameters().containsKey(MetaConstant.UNIT_DC)) {
                String clusterName = service.getParameters().get(MetaConstant.UNIT_DC);
                instances = namingService.selectInstances(service.getName(), List.of(clusterName),true);
            } else {
                instances = namingService.selectInstances(service.getName(),true);
            }
            return instances.stream().map(NacosServerInstanceUtil::convertNacosInstance).collect(Collectors.toList());
        } catch (NacosException e) {
            log.error("select nacos instance error", e);
            throw new RpcException(e, RpcException.NO_NACOS_INSTANCE);
        }
    }

    @Override
    public void subscribe(ServiceMeta service) {
        log.debug("nacos center subscribe service:" + JSON.toJSONString(service));
        try {
            if (service.getParameters().containsKey(MetaConstant.UNIT_DC)) {
                String clusterName = service.getParameters().get(MetaConstant.UNIT_DC);
                namingService.subscribe(service.getName(), service.getGroup(), List.of(clusterName), event -> {
                    handlerNacosEvent(service, event);
                });
            } else {
                namingService.subscribe(service.getName(), service.getGroup(), event -> {
                    handlerNacosEvent(service, event);
                });
            }
            // 初次订阅先啦一次数据以便fectchAll能查到数据
            nacosRegistryEvent.saveServiceInstanceMetas(service, fetchAllNacos(service));
        } catch (NacosException e) {
            log.error("subscribe nacos instance error", e);
            throw new RpcException(e, RpcException.NO_NACOS_INSTANCE);
        }
    }

    private void handlerNacosEvent(ServiceMeta service, Event event) {
        if (event instanceof NamingEvent) {
            List<Instance> instance = ((NamingEvent) event).getInstances();
            if (CollectionUtils.isNotEmpty(instance)) {
                log.debug("reveive nacos instances:" + JSON.toJSONString(instance));
               List<InstanceMeta> instanceMetas = instance.stream().map(NacosServerInstanceUtil::convertNacosInstance).collect(Collectors.toList());
               listenerEvent((listener -> listener.onSubscribe(service,instanceMetas)));
            }
        }
    }

    @Override
    public void unsubscribe(ServiceMeta service) {
        try {
            if (service.getParameters().containsKey(MetaConstant.UNIT_DC)) {
                String clusterName = service.getParameters().get(MetaConstant.UNIT_DC);
                namingService.unsubscribe(service.getName(), service.getGroup(), List.of(clusterName), event -> {
                    listenerEvent((listener -> listener.onUnSubscribe(service)));
                });
            } else {
                namingService.unsubscribe(service.getName(), service.getGroup(), event -> {
                    listenerEvent((listener -> listener.onUnSubscribe(service)));
                });
            }
        } catch (NacosException e) {
            log.error("unsubscribe nacos instance error", e);
            throw new RpcException(e, RpcException.NO_NACOS_INSTANCE);
        }
    }
}
