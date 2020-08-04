package com.hgy.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class MyHttpServer {

    private ServerBootstrap serverBootstrap;
    private Integer port;

    public MyHttpServer(Integer port)  {
        this.port = port;
        serverBootstrap = new ServerBootstrap();
    }

    public MyHttpServer() {
        this(8888);
    }

    public void start() {
        NioEventLoopGroup boss = new NioEventLoopGroup(1);
        NioEventLoopGroup work = new NioEventLoopGroup();

        try {
            serverBootstrap.group(boss, work)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(port)
                    .childHandler(new MyHttpInitializer());

            ChannelFuture bindFuture = serverBootstrap.bind().sync();
            ChannelFuture sync = bindFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
