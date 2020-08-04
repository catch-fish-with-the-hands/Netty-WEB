package com.hgy.staticweb;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class MyStaticWebServer {

    private ServerBootstrap serverBootstrap;
    private Integer port;

    public MyStaticWebServer(Integer port)  {
        this.port = port;
        serverBootstrap = new ServerBootstrap();
    }

    public MyStaticWebServer() {
        this(8888);
    }

    public void start() {
        NioEventLoopGroup boss = new NioEventLoopGroup(1);
        NioEventLoopGroup work = new NioEventLoopGroup();

        try {
            serverBootstrap.group(boss, work)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(port)
                    .childHandler(new MyStaticWebInitializer());

            ChannelFuture bindFuture = serverBootstrap.bind().sync();
            ChannelFuture sync = bindFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
