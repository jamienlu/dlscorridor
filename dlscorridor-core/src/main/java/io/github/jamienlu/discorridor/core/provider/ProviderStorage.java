package io.github.jamienlu.discorridor.core.provider;

import io.github.jamienlu.discorridor.core.meta.ProviderMeta;
import io.github.jamienlu.discorridor.common.meta.ServiceMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jamieLu
 * @create 2024-03-28
 */
public class ProviderStorage {
    /**
     * 服务注册信息  注册中心
     */
    private final List<ServiceMeta> skeltonRegs = new ArrayList<>();
    /**
     * 服务调用  被远端调用反射执行  接口全类名 -> 方法签名 -> 服务对象
     */
    private final Map<String, Map<String,ProviderMeta>> skeltonInvokers = new HashMap<>();

    public void storage(List<ServiceMeta> regs, Map<String, Map<String,ProviderMeta>> invokers) {
        skeltonRegs.addAll(regs);
        skeltonInvokers.putAll(invokers);
    }
    /**
     * 注册获取
     *
     * @return List<ServiceMeta>
     */
    public List<ServiceMeta> findSkeltonRegs() {
        return skeltonRegs;
    }

    /**
     * 被远端唤醒调用
     *
     * @param servicerName 服务接口名
     * @param methodSign 方法签名
     * @return ProviderMeta
     */
    public ProviderMeta findProviderMeta(String servicerName, String methodSign) {
        return skeltonInvokers.getOrDefault(servicerName, new HashMap<>()).get(methodSign);
    }

    /**
     * 容器关闭清除数据
     */
    public void cleanUp() {
        skeltonRegs.clear();
        skeltonInvokers.clear();
    }
}
