package cn.jamie.dlscorridor.core.transform.http;

import cn.jamie.dlscorridor.core.api.RpcRequest;
import cn.jamie.dlscorridor.core.api.RpcResponse;
import cn.jamie.dlscorridor.core.exception.RpcException;
import cn.jamie.dlscorridor.core.meta.InstanceMeta;
import cn.jamie.dlscorridor.core.transform.RpcTransform;
import cn.jamie.dlscorridor.core.util.HttpUtil;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author jamieLu
 * @create 2024-04-08
 */
@Slf4j
public class HttpRpcTransform implements RpcTransform {
    private final static MediaType JSON_TYPE = MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient okHttpClient;

    public HttpRpcTransform(HttpConf httpConf) {
        okHttpClient = new OkHttpClient.Builder().connectionPool(new ConnectionPool(httpConf.getMaxIdleCons(), httpConf.getAliveTime(), TimeUnit.MILLISECONDS))
            .readTimeout(httpConf.getReadOutTime(), TimeUnit.MILLISECONDS)
            .writeTimeout(httpConf.getWriteOutTime(), TimeUnit.MILLISECONDS)
            .connectTimeout(httpConf.getConOutTime(), TimeUnit.MILLISECONDS)
            .build();
    }

    @Override
    public RpcResponse transform(RpcRequest rpcRequest, InstanceMeta instanceMeta) {
        String res;
        try {
            res = postOkHttp(HttpUtil.convertIpAddressToHttp(instanceMeta.toAddress()), JSON.toJSONString(rpcRequest));
            return JSON.parseObject(res, RpcResponse.class);
        } catch (IOException e) {
            log.error("postOkHttp error", e);
            throw new RpcException(e, RpcException.HTTP_ERROR);
        }
    }



    public String postOkHttp(String url, String body) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(body, JSON_TYPE))
                .build();
        return Objects.requireNonNull(okHttpClient.newCall(request).execute().body()).string();

    }
}
