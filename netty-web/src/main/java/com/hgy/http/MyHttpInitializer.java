package com.hgy.http;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http2.Http2Codec;

public class MyHttpInitializer extends ChannelInitializer {
    protected void initChannel(Channel channel) throws Exception {
        // 添加HTTP协议的编解码处理器
        channel.pipeline().addLast(new HttpServerCodec());
        // 加入自己的处理器
        channel.pipeline().addLast(new MyHttpServerHandler());
    }
}
