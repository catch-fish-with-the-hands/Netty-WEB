package com.hgy.staticweb;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

public class MyStaticWebInitializer extends ChannelInitializer {
    protected void initChannel(Channel ch) throws Exception {
        // Http相关的编解码器
        ch.pipeline().addLast("http-decoder", new HttpRequestDecoder());
        ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));
        ch.pipeline().addLast("http-encoder", new HttpResponseEncoder());
        // 文件分块
        ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());

        // 自定义文件处理器
        ch.pipeline().addLast("http-fileHandler", new MyFileHandler());
    }
}
