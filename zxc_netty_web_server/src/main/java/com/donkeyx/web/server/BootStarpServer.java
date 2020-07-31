package com.donkeyx.web.server;

import com.donkeyx.web.Service.ResourcesService;
import com.donkeyx.web.handler.ServerHandler;
import com.donkeyx.web.initializer.HttpServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class BootStarpServer {


    public static void main(String[] args) {
        int port = ResourcesService.getInstance().getProperties("port") == null ? 8080 : Integer.valueOf(ResourcesService.getInstance().getProperties("port"));
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new HttpServerInitializer());

            Channel ch = b.bind(port).sync().channel();
            System.out.println("服务成功启动,请打开http://127.0.0.1:"+port);
            ch.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
