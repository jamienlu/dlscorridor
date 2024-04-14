package cn.jamie.discorridor.spring.boot.autoconfigure.constant;

/**
 * @author jamieLu
 * @create 2024-03-25
 */
public class AutoConfigurationConst {
    /**
     * The separator of property name
     */
    public static final String PROPERTY_NAME_SEPARATOR = ".";
    public static final String DISCORRIDOR_PREFIX = "discorridor";
    public static final String CONSUMER_PREFIX = DISCORRIDOR_PREFIX + PROPERTY_NAME_SEPARATOR + "consumer";
    public static final String PROVIDER_PREFIX = DISCORRIDOR_PREFIX + PROPERTY_NAME_SEPARATOR + "provider";
    public static final String REGISTRY_PREFIX = DISCORRIDOR_PREFIX + PROPERTY_NAME_SEPARATOR + "registry";
    public static final String TRANSFORM_NETTY = DISCORRIDOR_PREFIX + PROPERTY_NAME_SEPARATOR + "netty";
    public static final String LOADBALANCE_ROUND = "round";
    public static final String LOADBALANCE_RANDOM = "random";
    public static final String FILTER_TOKEN = "token";
    public static final String FILTER_CONTEXT = "context";
    public static final String FILTER_CACHE= "cache";

    public static final String RPC_RETRY = "app.retry";
    public static final String RPC_TIMEOUT = "app.timeout";
    public static final String RPC_FAULT_LIMIT = "app.faultLimit";
    public static final String RPC_HALF_DELAY = "app.halfOpenDelay";
    public static final String RPC_HALF_INIT_DELAY = "app.halfOpenInitialDelay";

    public static final String RPC_HTTP = "http";
    public static final String RPC_NETTY = "netty";
}
