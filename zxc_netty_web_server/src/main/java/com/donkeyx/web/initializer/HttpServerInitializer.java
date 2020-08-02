package com.donkeyx.web.initializer;

import com.donkeyx.web.handler.FileServerHandler;
import com.donkeyx.web.handler.ServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 *
 */
public class HttpServerInitializer  extends ChannelInitializer<SocketChannel> {
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        //HttpObjectAggregator HTTP 消息解码器， 作用时将多个消息转换为1个FullHttpRequest 或者 FullHttpResponse 对象
        /**
         * HttpRequestDecoder 会将每个 HTTP 消息转换为 多个消息对象
         * HttpResquest / HttpResponse
         * HttpContent
         * LastHttpContent
         */
        //将请求和应答消息编码或解码为HTTP消息
        socketChannel.pipeline().addLast(new HttpServerCodec());
        socketChannel.pipeline().addLast(new HttpObjectAggregator(65536));// 目的是将多个消息转换为单一的request或者response对象
        socketChannel.pipeline().addLast(new ChunkedWriteHandler());//目的是支持异步大文件传输（）
        socketChannel.pipeline().addLast("zxc-file-handler", new FileServerHandler());
        socketChannel.pipeline().addLast("zxc-handler", new ServerHandler(false));

    }
}
