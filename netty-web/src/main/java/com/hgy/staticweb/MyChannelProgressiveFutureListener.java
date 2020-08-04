package com.hgy.staticweb;


import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;

public class MyChannelProgressiveFutureListener implements ChannelProgressiveFutureListener {
    public void operationComplete(ChannelProgressiveFuture future)
            throws Exception {
        System.out.println("Transfer complete.");

    }
    public void operationProgressed(ChannelProgressiveFuture future,
                                    long progress, long total) throws Exception {
        if(total < 0)
            System.err.println("Transfer progress: " + progress);
        else
            System.err.println("Transfer progress: " + progress + "/" + total);
    }
}
