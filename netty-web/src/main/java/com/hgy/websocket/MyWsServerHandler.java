package com.hgy.websocket;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.EventExecutorGroup;

public class MyWsServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // 服务端处理数据
        System.out.println("服务端收到消息：" + msg.text());
        // 响应客户端
        ctx.channel().writeAndFlush(new TextWebSocketFrame("server msg: " + msg.text()));
    }
}
