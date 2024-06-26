package io.github.jamienlu.discorridor.common.exception;

/**
 * @author jamieLu
 * @create 2024-04-02
 */
public class RpcException extends RuntimeException {

    private String errcode;

    public RpcException() {
        super();
    }

    public RpcException(String errcode) {
        super(errcode);
        this.errcode = errcode;
    }

    public RpcException(Throwable cause, String errcode) {
        super(cause);
        this.errcode = errcode;
    }


    // X => 技术类异常：
    // Y => 业务类异常：
    // Z => unknown, 搞不清楚，再归类到X或Y
    public static final String SOCKET_TIMEOUT_EX = "X001" + "-" + "http_invoke_timeout";
    public static final String NO_SUCH_METHOD_EX = "X002" + "-" + "method_not_exists";
    public static final String NO_USE_METAINSTANCE  = "X003" + "-" + "instance_not_userful";
    public static final String NO_TOKEN  = "X004" + "-" + "too fast! no token use";
    public static final String NO_NACOS_SERVER  = "X005" + "-" + "nacos_server_error";
    public static final String NO_NACOS_INSTANCE  = "X006" + "-" + "nacos_instance_error";
    public static final String HTTP_ERROR  = "X007" + "-" + "http_error";
    public static final String NETTY_ERROR  = "X008" + "-" + "netty_error";
    public static final String SERVER_OVERLOAD  = "X009" + "-" + "provider over tpsLimit";
    public static final String UNKNOWN_EX = "Z001" + "-" + "unknown";
}
