package com.donkeyx.web.handler;

import com.donkeyx.web.service.ResourcesService;
import com.donkeyx.web.util.DonkeyHttpUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;


import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.StringUtil;

import java.util.Set;

import static io.netty.handler.codec.http.HttpResponseStatus.*;


public class ServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }


    public ServerHandler(boolean isAutoRelease){
        super(isAutoRelease);
    }


    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest request) throws Exception {
        try {
            if(!request.decoderResult().isSuccess()){
                DonkeyHttpUtil.writeResponse(request, BAD_REQUEST, channelHandlerContext);
                return;
            }
            if(request.method() != HttpMethod.GET){
                DonkeyHttpUtil.writeResponse(request, METHOD_NOT_ALLOWED, channelHandlerContext);
                return;
            }
            DonkeyHttpUtil.writeResponse(request, OK, channelHandlerContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}