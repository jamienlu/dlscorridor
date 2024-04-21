package io.github.jamienlu.transform.http;

import io.github.jamienlu.discorridor.common.api.RpcRequest;
import io.github.jamienlu.discorridor.common.api.RpcResponse;
import io.github.jamienlu.discorridor.common.exception.RpcException;
import io.github.jamienlu.discorridor.common.meta.InstanceMeta;
import io.github.jamienlu.discorridor.serialization.api.SerializationService;
import io.github.jamienlu.transform.api.RpcTransform;
import io.github.jamienlu.transform.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author jamieLu
 * @create 2024-04-08
 */
@Slf4j
public class HttpRpcTransform implements RpcTransform {
    private final MediaType mediaType;
    private final OkHttpClient okHttpClient;
    private final SerializationService serializationService;

    public HttpRpcTransform(HttpConf httpConf, SerializationService serializationService) {

        okHttpClient = new OkHttpClient.Builder().connectionPool(new ConnectionPool(httpConf.getMaxIdleCons(), httpConf.getAliveTime(), TimeUnit.MILLISECONDS))
            .readTimeout(httpConf.getReadOutTime(), TimeUnit.MILLISECONDS)
            .writeTimeout(httpConf.getWriteOutTime(), TimeUnit.MILLISECONDS)
            .connectTimeout(httpConf.getConOutTime(), TimeUnit.MILLISECONDS)
            .build();
        this.serializationService = serializationService;
        mediaType = switch (serializationService.getClass().getSimpleName()) {
            case "FastJson2Serializer" ->  MediaType.get("application/json; charset=utf-8");
            case "ProtobufSerializer" -> MediaType.get("application/x-protobuf; charset=utf-8");
            default ->  MediaType.get("application/json; charset=utf-8");
        };
    }

    @Override
    public RpcResponse transform(RpcRequest rpcRequest, InstanceMeta instanceMeta) {
        try {
            return postOkHttp(HttpUtil.convertIpAddressToHttp(instanceMeta.toAddress()), rpcRequest);
        } catch (IOException e) {
            log.error("postOkHttp error", e);
            throw new RpcException(e, RpcException.HTTP_ERROR);
        }
    }



    public RpcResponse postOkHttp(String url, RpcRequest rpcRequest) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(serializationService.serialize(rpcRequest), mediaType))
                .build();
        ResponseBody response = okHttpClient.newCall(request).execute().body();
        return serializationService.deserialize(response.bytes(), RpcResponse.class);
    }
}
