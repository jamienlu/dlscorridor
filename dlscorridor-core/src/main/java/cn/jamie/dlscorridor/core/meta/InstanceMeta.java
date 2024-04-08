package cn.jamie.dlscorridor.core.meta;

import com.alibaba.fastjson2.JSON;
import com.google.common.base.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * 注册的服务实例元数据
 *
 * @author jamieLu
 * @create 2024-03-20
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstanceMeta {
    private String schema;
    private String host;
    private Integer port;
    private String context;
    // 上下线
    @Builder.Default
    private boolean status = true;
    @Builder.Default
    private Map<String,String> parameters = new HashMap<>();

    public InstanceMeta(String schema, String host, Integer port, String context) {
        this.schema = schema;
        this.host = host;
        this.port = port;
        this.context = context;
    }
    public String toPath() {
        return String.format("%s_%d", host, port);
    }

    public String toAddress() {
        return String.format("%s:%d", host, port);
    }

    public static InstanceMeta pathToInstance(String path) {
        String[] paras = path.split("_", -1);
        assert paras.length > 1;
        return InstanceMeta.builder().host(paras[0]).port(Integer.valueOf(paras[1])).build();
    }
    public void addMeta(String key, String value) {
        parameters.put(key,value);
    }
    public String toMetas() {
        return JSON.toJSONString(this.getParameters());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InstanceMeta that = (InstanceMeta) o;
        return Objects.equal(host, that.host) && Objects.equal(port, that.port);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(host, port);
    }
}
