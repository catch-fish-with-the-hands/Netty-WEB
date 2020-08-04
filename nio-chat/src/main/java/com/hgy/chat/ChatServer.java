package com.hgy.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * 群聊服务器
 */
public class ChatServer {

    private ServerSocketChannel serverSocketChannel;
    private Selector selector;


    /**
     * 初始化服务端
     */
    public ChatServer() {
        try {
            // 创建Selector以及ServerSocketChannel
            selector = Selector.open();
            serverSocketChannel = serverSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(8888));
            //将服务端监听通道注册到Selector中
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 监听客户端操作
     */
    public void listener() {
        while (true) {
            try {
                if (selector.select(1000) == 0) {
                    continue;
                }
                //获得所有有事件的key
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    //如果当前key是处理链接类型
                    if (key.isAcceptable()) {
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ);
                    }
                    // 当前链接是读数据类型
                    if (key.isReadable()) {
                        readData(key);
                    }
                    iterator.remove();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 读取数据并群发给所有的用户
     * @param key
     */
    private void readData(SelectionKey key) {
        try {
            if (key.isReadable()) {
                SocketChannel channel = (SocketChannel) key.channel();
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                channel.read(byteBuffer);
                String s = new String(byteBuffer.array());
                // 写到其他所有客户端
                sendData2All(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 群发给所有的用户
     * @param msg 需要发送的消息
     */
    private void sendData2All(String msg) {
        try {
            // 当前在selector上注册的所有key就是所有用户
            Set<SelectionKey> keys = selector.keys();
            for (SelectionKey key : keys) {
                // 获取每个用户的通道
                SelectableChannel channel = key.channel();
                // 实现数据发送
                if (channel instanceof SocketChannel) {
                    System.out.println(":::" + msg);
                    ByteBuffer byteBuffer = ByteBuffer.wrap(msg.getBytes());
                    SocketChannel socketChannel = (SocketChannel) channel;
                    socketChannel.write(byteBuffer);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer();
        chatServer.listener();
    }
}
