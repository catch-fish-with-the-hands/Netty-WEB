package com.hgy.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class ChatClient {
    public static void main(String[] args) throws Exception {

        // 客户端代码, 建立连接
        Selector selector = Selector.open();
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8888));
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        // 开启一个新的线程轮询当前客户是否有可读消息
        new Thread(() -> {
            while (true) {
                try {
                    int select = selector.select(1000);
                    // 有可读消息进行解析打印
                    if (select > 0) {
                        for (SelectionKey key : selector.selectedKeys()) {
                            if (key.isReadable()) {
                                SocketChannel channel = (SocketChannel) key.channel();
                                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                                channel.read(byteBuffer);
                                System.out.println(":==:" + new String(byteBuffer.array()));
                                // 写到其他所有客户端
                                System.out.println(new String(byteBuffer.array()));
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // 主线程中循环获取用户输入的聊天消息
        while(true) {
            Scanner scanner = new Scanner(System.in);
            //发送用户的消息
            socketChannel.write(ByteBuffer.wrap(scanner.nextLine().getBytes()));
        }
    }
}
