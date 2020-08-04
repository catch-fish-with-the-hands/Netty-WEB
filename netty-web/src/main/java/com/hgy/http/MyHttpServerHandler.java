package com.hgy.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.EventExecutorGroup;

public class MyHttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {
    protected void channelRead0(
            ChannelHandlerContext ctx,
            HttpObject httpObject) throws Exception {
        //此处HttpObject默认是HttpRequest
        if (httpObject instanceof HttpRequest) {
            //解析请求
            HttpRequest request = (HttpRequest) httpObject;
            System.out.println(request.uri());
            ByteBuf byteBuf = Unpooled.copiedBuffer("hello world netty http", CharsetUtil.UTF_8);

            //封装响应数据
            HttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,
                    byteBuf);

            response.headers()
                    .set(HttpHeaderNames.CONTENT_TYPE, "text/plain")
                    .set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());

            ctx.writeAndFlush(response);
        }
    }
}
