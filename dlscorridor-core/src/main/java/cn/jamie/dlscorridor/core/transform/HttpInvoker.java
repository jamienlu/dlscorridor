package cn.jamie.dlscorridor.core.transform;

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
 * @create 2024-03-20
 */
public class HttpInvoker {
    private final static MediaType JSON_TYPE = MediaType.get("application/json; charset=utf-8");
    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient.Builder().connectionPool(new ConnectionPool(16,60, TimeUnit.SECONDS))
            .readTimeout(1,TimeUnit.SECONDS)
            .writeTimeout(1,TimeUnit.SECONDS)
            .connectTimeout(1,TimeUnit.SECONDS)
            .build();
    public String postOkHttp(String url, String body) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(body,JSON_TYPE))
                .build();
        return Objects.requireNonNull(OK_HTTP_CLIENT.newCall(request).execute().body()).string();

    }
}
