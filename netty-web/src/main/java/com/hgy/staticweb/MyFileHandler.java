package com.hgy.staticweb;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;

import javax.activation.MimetypesFileTypeMap;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class MyFileHandler extends SimpleChannelInboundHandler<HttpObject> {


    private String basePath = "D:\\";

    protected void channelRead0(
            ChannelHandlerContext ctx, HttpObject httpObject) {
        HttpRequest request = (HttpRequest) httpObject;
        String uri = request.getUri();
        // 默认过滤处理
        if (defaultFilterRequest(uri)) {
            sendError(ctx);
            return;
        }
        // 处理文件发送
        try {
            String fileName = uri.substring(uri.lastIndexOf("/"));
            sendFile2Browser(fileName, ctx);
        } catch (Exception e) {
            e.printStackTrace();
            sendError(ctx);
        }
    }

    /**
     * 发送数据到浏览器
     * @param fileName
     * @param ctx
     * @throws Exception
     */
    private void sendFile2Browser(String fileName, ChannelHandlerContext ctx) throws Exception {
        // 获取文件输入流
        RandomAccessFile randomAccessFile = new RandomAccessFile(basePath + fileName, "r");
        long fileLength = randomAccessFile.length();
        // 封装响应体
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, fileLength);
        // 动态的根据文件名获取媒体类型
        MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, mimetypesFileTypeMap.getContentType(fileName));
        ctx.write(response);
        //真正写数据
        ChannelFuture sendFileFuture = null;
        //每一块儿文件的大小
        ChunkedFile chunkedFile = new ChunkedFile(randomAccessFile, 0, fileLength, 8192);
        sendFileFuture = ctx.write(chunkedFile, ctx.newProgressivePromise());
        // 添加文件传输进度监控可有可无
        sendFileFuture.addListener(new MyChannelProgressiveFutureListener());
        // 响应结尾数据
        ChannelFuture lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
    }

    /**
     * 向前端发送404错误
     * @param ctx
     */
    private void sendError(ChannelHandlerContext ctx) {
        ByteBuf byteBuf = Unpooled.copiedBuffer("Failure: Not Found \r\n", CharsetUtil.UTF_8);
        HttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.NOT_FOUND,
                byteBuf
        );
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=UTF-8");
        ctx.writeAndFlush(response);

    }

    /**
     * 判断当前请求是否需要过滤
     * @param uri 请求uri
     * @return
     */
    private boolean defaultFilterRequest(String uri) {
        if (!uri.contains(".") || uri.contains("favicon.ico")) {
            return true;
        }
        return false;
    }


    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }
}
