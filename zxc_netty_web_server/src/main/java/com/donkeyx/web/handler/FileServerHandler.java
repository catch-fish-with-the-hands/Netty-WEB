package com.donkeyx.web.handler;

import com.donkeyx.web.service.ResourcesService;
import com.donkeyx.web.util.DonkeyHttpUtil;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedNioFile;
import io.netty.util.internal.StringUtil;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.net.URLDecoder;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class FileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {



    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest request) throws Exception {
        //request.retain();
        HttpResponse response = null;
        RandomAccessFile randomAccessFile = null;
        try{
            // 状态为1xx的话，继续请求
            if (HttpHeaders.is100ContinueExpected(request)) {
                send100Continue(channelHandlerContext);
            }
            String uri = request.uri();
            if(!uri.endsWith(".js") && !uri.endsWith(".css") && !uri.endsWith(".html")){
                channelHandlerContext.fireChannelRead(request);
                return;
            }
            // hello/a.js
            int index = uri.lastIndexOf("/") + 1;
            if(index == -1){
               DonkeyHttpUtil.writeResponse(request, OK, channelHandlerContext);
               return;
            }
            String filename = uri.substring(index);
            uri = uri.substring(0, index-1);
            String path = ResourcesService.getInstance().getPath(uri);
            if(StringUtil.isNullOrEmpty(path)){
                DonkeyHttpUtil.writeResponse(request, NOT_FOUND, channelHandlerContext);
                return;
            }
            String fullPath = path+ "/"+filename;
            File file = new File(fullPath);
            try {
                randomAccessFile = new RandomAccessFile(file, "r");
            } catch (FileNotFoundException e) {
                DonkeyHttpUtil.writeResponse(request, NOT_FOUND, channelHandlerContext);
                e.printStackTrace();
                return;
            }

            if(!file.exists() || file.isHidden()){
                DonkeyHttpUtil.writeResponse(request, NOT_FOUND, channelHandlerContext);
                return;
            }
            long fileLength = randomAccessFile.length();
            response = new DefaultHttpResponse(request.protocolVersion(), HttpResponseStatus.OK);

            setContentType(response, file);
            boolean keepAlive =  HttpUtil.isKeepAlive(request);
            if (keepAlive) {
                response.headers().set(HttpHeaderNames.CONTENT_LENGTH, fileLength);
                response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            }
            channelHandlerContext.write(response);


            ChannelFuture sendFileFuture = channelHandlerContext.write(new ChunkedNioFile(randomAccessFile.getChannel()), channelHandlerContext.newProgressivePromise());
            // 写入文件尾部
            sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
                @Override
                public void operationProgressed(ChannelProgressiveFuture future,
                                                long progress, long total) {
                    if (total < 0) { // total unknown
                        System.out.println("Transfer progress: " + progress);
                    } else {
                        System.out.println("Transfer progress: " + progress + " / "
                                + total);
                    }
                }

                @Override
                public void operationComplete(ChannelProgressiveFuture future)
                        throws Exception {
                    System.out.println("Transfer complete.");
                }


            });
            ChannelFuture lastContentFuture =  channelHandlerContext.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            if (!keepAlive) {
                lastContentFuture.addListener(ChannelFutureListener.CLOSE);
            }
        }finally {
            /*if(randomAccessFile != null){
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }*/
        }
    }

    private void setContentType(HttpResponse response, File file){
        //MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        if(file.getName().endsWith(".js")){
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/x-javascript");
        }else if(file.getName().endsWith(".css")){
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/css; charset=UTF-8");
        }else if (file.getName().endsWith(".html")){
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
        }
/*        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        response.headers().set(CONTENT_TYPE,
                mimeTypesMap.getContentType(file.getPath()));*/
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        ctx.writeAndFlush(response);
    }
}
