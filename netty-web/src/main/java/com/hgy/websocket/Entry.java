package com.hgy.websocket;

public class Entry {
    public static void main(String[] args) throws Exception {
        WsNettyServer wsNettyServer = new WsNettyServer();
        wsNettyServer.start();
    }
}
