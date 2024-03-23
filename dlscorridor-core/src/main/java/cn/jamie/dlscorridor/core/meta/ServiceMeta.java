package cn.jamie.dlscorridor.core.meta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    public String toPath() {
        return String.format("%s_%s_%s_%s_%s",app,namespace,env,name,version);
    }
    public boolean validVersion(String useVersion) {
        // 版本号比较算法
        return true;
    }


}
