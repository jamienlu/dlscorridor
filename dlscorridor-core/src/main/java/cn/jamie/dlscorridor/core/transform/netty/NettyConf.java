package cn.jamie.dlscorridor.core.transform.netty;

import lombok.Data;

/**
 * @author jamieLu
 * @create 2024-04-12
 */
@Data
public class NettyConf {
    private boolean enable = false;
    private Integer port = 9001;
    private Integer writeIdleTime = 0;
    private Integer readIdleTime = 0;
    private Integer closeIdleTime = 60000;
}
