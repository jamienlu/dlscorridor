package cn.jamie.discorridor.spring.boot.autoconfigure.bean;

import cn.jamie.dlscorridor.core.constant.MetaConstant;
import lombok.Data;

/**
 * @author jamieLu
 * @create 2024-03-28
 */
@Data
public class AppConf {
    // 环境
    private String env = "dev";
    // 域
    private String namespace = "public";
    // 组
    private String group = MetaConstant.GROUP_DEFAULT;
    // 集群
    private String dc = "cd";
    // 集群单元
    private String unit = "A001";
    // 流控 tps
    private Integer tc = 100;
    // 灰度
    private boolean gray = false;
    // 序列化
    private String serialization = "fastjson2";

}
