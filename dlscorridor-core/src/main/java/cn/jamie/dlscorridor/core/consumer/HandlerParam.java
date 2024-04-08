package cn.jamie.dlscorridor.core.consumer;

import lombok.Data;

import java.util.Map;

/**
 * @author jamieLu
 * @create 2024-04-08
 */
@Data
public class HandlerParam {
    // 重试次数
    private Integer retry;
    // 超时时间
    private Integer timeout;
    // 多少次异常视为故障
    private Integer faultLimit;
    // 故障探活时间周期
    private Integer halfOpenDelay;
    // 故障探活延迟时间
    private Integer halfOpenInitialDelay;

    private HandlerParam() {

    }
    public static HandlerParam getInstance(Map<String,String> parameters) {
        HandlerParam handlerParam = new HandlerParam();
        handlerParam.setRetry(Integer.valueOf(parameters.getOrDefault("app.retry", "1")));
        handlerParam.setTimeout(Integer.valueOf(parameters.getOrDefault("app.timeout", "5000")));
        handlerParam.setFaultLimit(Integer.valueOf(parameters.getOrDefault("app.faultLimit", "10")));
        handlerParam.setHalfOpenDelay(Integer.valueOf(parameters.getOrDefault("app.halfOpenDelay", "60")));
        handlerParam.setHalfOpenInitialDelay(Integer.valueOf(parameters.getOrDefault("app.halfOpenInitialDelay", "10")));
        return handlerParam;
    }

}
