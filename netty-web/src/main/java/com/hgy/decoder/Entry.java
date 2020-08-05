package com.hgy.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;

public class Entry {
    public static void main(String[] args) {
        ChannelInitializer i = new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel ch) throws Exception {
                ch.pipeline().addLast(new MyIntegerDecoder());
                ch.pipeline().addLast(new MyIntegerHandler());
            }
        };

        EmbeddedChannel channel = new EmbeddedChannel(i);
        for (int i1 = 0; i1 < 100; i1++) {
            ByteBuf byteBuf = Unpooled.buffer();
            byteBuf.writeInt(i1);
            channel.writeInbound(byteBuf);
        }
    }
}
