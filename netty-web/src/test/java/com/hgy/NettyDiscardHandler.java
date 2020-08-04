package com.hgy;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutorGroup;

public class NettyDiscardHandler extends ChannelInboundHandlerAdapter {

    /**
     * 处理通道中的数据
     * @param ctx 当前请求上下文
     * @param msg 消息
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 客户端消息
        ByteBuf byteBuf = (ByteBuf) msg;
        try {
            while (byteBuf.isReadable()) {
                System.out.println((char) byteBuf.readByte());
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }
}
