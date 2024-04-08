package cn.jamie.dlscorridor.core.transform;

import cn.jamie.dlscorridor.core.api.RpcRequest;
import cn.jamie.dlscorridor.core.api.RpcResponse;
import cn.jamie.dlscorridor.core.exception.RpcException;
import cn.jamie.dlscorridor.core.util.HttpUtil;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author jamieLu
 * @create 2024-04-08
 */
@Slf4j
public class HttpRpcTransform implements RpcTransform {
    @Override
    public RpcResponse transform(RpcRequest rpcRequest, String urlAddress) {
        String res;
        try {
            res = HttpInvoker.postOkHttp(HttpUtil.convertIpAddressToHttp(urlAddress), JSON.toJSONString(rpcRequest));
            return JSON.parseObject(res, RpcResponse.class);
        } catch (IOException e) {
            log.error("postOkHttp error", e);
            throw new RpcException(e, RpcException.HTTP_ERROR);
        }
    }
}
