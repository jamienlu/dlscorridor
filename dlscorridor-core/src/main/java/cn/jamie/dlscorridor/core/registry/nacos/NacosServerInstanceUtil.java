package cn.jamie.dlscorridor.core.registry.nacos;

import cn.jamie.dlscorridor.core.constant.MetaConstant;
import cn.jamie.dlscorridor.core.meta.InstanceMeta;
import cn.jamie.dlscorridor.core.meta.ServiceMeta;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.Service;

import java.util.Map;

/**
 * @author jamieLu
 * @create 2024-04-06
 */
public class NacosServerInstanceUtil {
    public static Service createNacosServer(ServiceMeta serviceMeta) {
        Service service = new Service();
        service.setAppName(serviceMeta.getApp());
        service.setName(serviceMeta.getName());
        service.setGroupName(serviceMeta.getGroup());
        service.setMetadata(serviceMeta.getParameters());
        service.getMetadata().put(MetaConstant.VERSION, serviceMeta.getVersion());
        return service;
    }

    public static ServiceMeta convertNacosServer(Service service) {
        Map<String,String> metas = service.getMetadata();
        String version = metas.remove(MetaConstant.VERSION);
        return ServiceMeta.builder().app(service.getAppName()).name(service.getName())
            .group(service.getGroupName()).parameters(service.getMetadata()).version(version).build();
    }

    public static Instance createNacosInstance(String serviceName, InstanceMeta instanceMeta) {
        Instance instance = new Instance();
        instance.setIp(instanceMeta.getHost());
        instance.setPort(instanceMeta.getPort());
        instance.setMetadata(instanceMeta.getParameters());
        instance.setServiceName(serviceName);
        return instance;
    }
    public static InstanceMeta convertNacosInstance(Instance instance) {
        return InstanceMeta.builder().host(instance.getIp()).port(instance.getPort())
            .status(instance.isHealthy()).parameters(instance.getMetadata()).build();
    }
    public static Instance createClusterNacosInstance(String clusterName, String serviceName, InstanceMeta instanceMeta) {
        Instance instance = createNacosInstance(serviceName, instanceMeta);
        instance.setClusterName(clusterName);
        return instance;
    }
}