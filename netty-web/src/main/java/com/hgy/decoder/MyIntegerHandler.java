package com.hgy.decoder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class MyIntegerHandler extends SimpleChannelInboundHandler<Integer> {
    protected void channelRead0(ChannelHandlerContext ctx, Integer msg) throws Exception {
        System.out.println("handler: " + msg);
    }
}
