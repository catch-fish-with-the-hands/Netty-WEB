package com.hgy;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyDiscardServer {


    public static void main(String[] args) throws InterruptedException {
        //启动管理器
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        // workGroup and bossGroup
        // 处理链接建立
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        // 消息处理
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            serverBootstrap
                    .group(bossGroup, workGroup) //装配线程
                    .channel(NioServerSocketChannel.class) //通道类型
                    .localAddress(8888) //绑定端口
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            // 添加每个用户通道的处理器，处理用户的请求
                            socketChannel.pipeline().addLast(new NettyDiscardHandler());
                        }
                    });

            ChannelFuture channelFuture = serverBootstrap.bind().sync();
            ChannelFuture channelFuture1 = channelFuture.channel().closeFuture();
            channelFuture1.sync();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }

    }





}
