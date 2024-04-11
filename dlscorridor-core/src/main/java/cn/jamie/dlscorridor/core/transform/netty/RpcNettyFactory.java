package cn.jamie.dlscorridor.core.transform.netty;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.ThreadFactory;

/**
 * @author jamieLu
 * @create 2024-04-09
 */
public class RpcNettyFactory {

    public static EventLoopGroup createClientEvenGroup() {
        return new NioEventLoopGroup(1, new DefaultThreadFactory("client-io", true));
    }
    public static EventLoopGroup createBossGroup() {
        return eventLoopGroup(1, "boss-io");
    }

    public static EventLoopGroup createWorkerGroup() {
        return eventLoopGroup(6,"work-io");
    }
    public static boolean isEpoll() {
        String nettyPoll = System.getProperty("netty.epoll.enable");
        if (Boolean.parseBoolean(nettyPoll)) {
            String osName = System.getProperty("os.name");
            return osName.toLowerCase().contains("linux") && Epoll.isAvailable();
        }
        return false;
    }

    public static EventLoopGroup eventLoopGroup(int threads, String threadFactoryName) {
        ThreadFactory threadFactory = new DefaultThreadFactory(threadFactoryName, true);
        return isEpoll() ? new EpollEventLoopGroup(threads, threadFactory) :
                new NioEventLoopGroup(threads, threadFactory);
    }
    public static Class<? extends ServerSocketChannel> serverSocketChannelClass() {
        return isEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class;
    }

    public static Class<? extends SocketChannel> socketChannelClass() {
        return isEpoll() ? EpollSocketChannel.class : NioSocketChannel.class;
    }
}
