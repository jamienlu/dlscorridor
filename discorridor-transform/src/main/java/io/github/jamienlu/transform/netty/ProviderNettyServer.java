package io.github.jamienlu.transform.netty;

import io.github.jamienlu.discorridor.common.api.RpcInvokeHandler;
import io.github.jamienlu.discorridor.common.api.RpcRequest;
import io.github.jamienlu.discorridor.serialization.api.SerializationService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author jamieLu
 * @create 2024-04-12
 */
@Slf4j
public class ProviderNettyServer {
    private final RpcInvokeHandler providerInvoker;
    private final NettyConf nettyConf;
    private final SerializationService serializationService;

    public ProviderNettyServer(RpcInvokeHandler providerInvoker, NettyConf nettyConf,SerializationService serializationService) {
        this.providerInvoker = providerInvoker;
        this.nettyConf = nettyConf;
        this.serializationService = serializationService;
    }

    public void startServer() throws Exception {
        EventLoopGroup bossGroup = NettyFactory.createBossGroup();
        EventLoopGroup workerGroup = NettyFactory.createWorkerGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NettyFactory.serverSocketChannelClass())
                    .option(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                    .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline()
                                    .addLast("decoder", new MessageEncoder(serializationService))
                                    .addLast("encoder", new MessageDecoder(RpcRequest.class, serializationService))
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
