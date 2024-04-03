package cn.jamie.dlscorridor.core.meta;

import com.alibaba.fastjson2.JSON;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 注册中心服务元数据
 *
 * @author jamieLu
 * @create 2024-03-20
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceMeta {
    private String app;
    private String namespace;
    private String env;
    private String name;
    private String version;
    @Builder.Default
    private Map<String,String> parameters = Map.of("meta","services");

    public String toPath() {
        return String.format("%s_%s_%s_%s",app,namespace,env,name);
    }

    public String toMetas() {
        return JSON.toJSONString(this.getParameters());
    }

}
