package cn.jamie.dlscorridor.core.provider;

import cn.jamie.dlscorridor.core.exception.RpcException;
import cn.jamie.dlscorridor.core.serialization.SerializationService;
import cn.jamie.dlscorridor.core.transform.netty.ProviderChannelHandler;
import cn.jamie.dlscorridor.core.transform.netty.RpcNettyFactory;
import cn.jamie.dlscorridor.core.transform.netty.RpcRequestDecoder;
import cn.jamie.dlscorridor.core.transform.netty.RpcResponseEncoder;
import cn.jamie.dlscorridor.core.transform.netty.NettyConf;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author jamieLu
 * @create 2024-04-12
 */
@Slf4j
public class ProviderNettyServer {
    private final ProviderInvoker providerInvoker;
    private final NettyConf nettyConf;
    private final SerializationService serializationService;

    public ProviderNettyServer(ProviderInvoker providerInvoker, NettyConf nettyConf,SerializationService serializationService) {
        this.providerInvoker = providerInvoker;
        this.nettyConf = nettyConf;
        this.serializationService = serializationService;
    }
    // 初始化 挂载服务到netty server
    @PostConstruct
    public void mountNettyInvokers() {
        Executors.newFixedThreadPool(1).submit(() -> {
            try {
                startServer();
                log.info("mount provider netty server success!");
            } catch (Exception e) {
                throw new RpcException(e.getCause(),RpcException.NETTY_ERROR);
            }
        });
    }

    private void startServer() throws Exception {
        EventLoopGroup bossGroup = RpcNettyFactory.createBossGroup();
        EventLoopGroup workerGroup = RpcNettyFactory.createWorkerGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(RpcNettyFactory.serverSocketChannelClass())
                    .option(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                    .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline()
                                    .addLast("decoder", new RpcRequestDecoder(serializationService))
                                    .addLast("encoder", new RpcResponseEncoder(serializationService))
                                    .addLast("server-idle-handler", new IdleStateHandler(nettyConf.getReadIdleTime(), nettyConf.getWriteIdleTime(), nettyConf.getCloseIdleTime(), TimeUnit.MILLISECONDS))
                                    .addLast("handler", new ProviderChannelHandler(providerInvoker));
                        }
                    });
            // 绑定端口，开始接收进来的连接
            ChannelFuture f = bootstrap.bind(nettyConf.getPort()).sync();
            // 等待服务器 socket 关闭
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
