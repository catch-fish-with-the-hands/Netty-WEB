package com.hgy.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class WsNettyServer {

    private ServerBootstrap serverBootstrap;

    public void start() throws Exception {
        // 创建线程组
        EventLoopGroup boss = new NioEventLoopGroup(1);
        EventLoopGroup work = new NioEventLoopGroup();

        // 组装组件
        serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boss, work)
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(8888))
                .childHandler(new MyWsServerInitializer());

        ChannelFuture channelFuture = serverBootstrap.bind().sync();
        ChannelFuture closeFuture = channelFuture.channel().closeFuture().sync();
    }
}
