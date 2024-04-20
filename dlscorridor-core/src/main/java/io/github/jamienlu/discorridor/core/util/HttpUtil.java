package io.github.jamienlu.discorridor.core.util;

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
 * http 工具类
 *
 * @author jamieLu
 * @create 2024-03-12
 */
@Slf4j
public class HttpUtil {

    public static String convertIpAddressToHttp(String url) {
        return "http://" + url + "/rpc/services";
    }
}
