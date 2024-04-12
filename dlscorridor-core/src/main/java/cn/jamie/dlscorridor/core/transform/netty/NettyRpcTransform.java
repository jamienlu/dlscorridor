package cn.jamie.dlscorridor.core.transform.netty;

import cn.jamie.dlscorridor.core.api.RpcRequest;
import cn.jamie.dlscorridor.core.api.RpcResponse;
import cn.jamie.dlscorridor.core.exception.RpcException;
import cn.jamie.dlscorridor.core.meta.InstanceMeta;
import cn.jamie.dlscorridor.core.serialization.SerializationService;
import cn.jamie.dlscorridor.core.transform.RpcTransform;
import com.alibaba.fastjson2.JSON;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author jamieLu
 * @create 2024-04-11
 */
@Slf4j
public class NettyRpcTransform implements RpcTransform {
    private final NettyConf nettyConf;

    private final SerializationService serializationService;
    public NettyRpcTransform(NettyConf nettyConf,SerializationService serializationService) {
        this.nettyConf = nettyConf;
        this.serializationService = serializationService;
    }

    @Override
    public RpcResponse transform(RpcRequest rpcRequest, InstanceMeta instanceMeta) {
        String host = instanceMeta.getHost();
        String nettyPort = instanceMeta.searchMeta("port");
        try {
            Integer port = Integer.parseInt(nettyPort);
            return sendMsg(host, port, rpcRequest);
        } catch (Exception e) {
            return RpcResponse.builder().ex(new RpcException(e.getCause(),RpcException.NETTY_ERROR)).build();
        }
    }

    private RpcResponse sendMsg(String host, Integer port, RpcRequest msg) {
        ConsumerChannelHandler consumerChannelHandler = new ConsumerChannelHandler();
        EventLoopGroup clienGroup = RpcNettyFactory.createClientEvenGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(clienGroup)
                    .channel(RpcNettyFactory.socketChannelClass())
                    .option(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline()
                                    .addLast("decoder", new RpcDecoder(RpcResponse.class, serializationService))
                                    .addLast("encoder", new RpcEncoder(serializationService))
                                    .addLast("server-idle-handler", new IdleStateHandler(nettyConf.getReadIdleTime(), nettyConf.getWriteIdleTime(), nettyConf.getCloseIdleTime(), TimeUnit.MILLISECONDS))
                                    .addLast("handler",consumerChannelHandler);
                        }
                    });
            Channel channel = bootstrap.connect(host, port).sync().channel();
            channel.writeAndFlush(msg);
            channel.closeFuture().sync();
            RpcResponse response = consumerChannelHandler.receiveResponse();
            log.debug("netty sendMsg result:" + JSON.toJSONString(response));
            return response;
        } catch (Exception e) {
            log.error("netty transform message error!", e);
            return RpcResponse.builder().ex(new RpcException(e.getCause(), RpcException.NETTY_ERROR)).build();
        } finally {
            clienGroup.shutdownGracefully();
        }
    }
}
