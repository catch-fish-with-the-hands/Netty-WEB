package com.donkeyx.web.initializer;

import com.donkeyx.web.handler.ServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 *
 */
public class HttpServerInitializer  extends ChannelInitializer<SocketChannel> {
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().addLast("http-decoder", new HttpRequestDecoder());

        //HttpObjectAggregator HTTP 消息解码器， 作用时将多个消息转换为1个FullHttpRequest 或者 FullHttpResponse 对象
        /**
         * HttpRequestDecoder 会将每个 HTTP 消息转换为 多个消息对象
         * HttpResquest / HttpResponse
         * HttpContent
         * LastHttpContent
         */
        socketChannel.pipeline().addLast("http-AGGREGATOR", new HttpObjectAggregator(65535));
        socketChannel.pipeline().addLast("http-encoder", new HttpResponseEncoder());
        socketChannel.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
        socketChannel.pipeline().addLast("zxc-handler", new ServerHandler());
    }
}
