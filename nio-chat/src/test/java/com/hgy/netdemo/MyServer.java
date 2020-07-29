package com.hgy.netdemo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class MyServer {

    private ServerSocketChannel serverSocketChannel;
    private Selector selector;

    public void start(Integer port) throws Exception{
        serverSocketChannel = ServerSocketChannel.open();
        selector = Selector.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        startListener();
    }

    private void startListener() throws Exception {
        while (true) {
            if (selector.select(1000) == 0) {
                System.out.println("current not exists task");
                continue;
            }
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if (key.isAcceptable())
                    handleConnection();
                if (key.isReadable())
                    handleMsg(key);
                iterator.remove();
            }
        }
    }

    private void handleMsg(SelectionKey key) throws Exception {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer attachment = (ByteBuffer) key.attachment();
        channel.read(attachment);
        System.out.println("current msg: " + new String(attachment.array()));
    }

    private void handleConnection() throws Exception {
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
    }


    public static void main(String[] args) throws Exception {
        MyServer myServer = new MyServer();
        myServer.start(8888);
    }
}
