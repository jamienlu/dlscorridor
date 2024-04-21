package io.github.jamienlu.transform.util;

import lombok.extern.slf4j.Slf4j;

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
