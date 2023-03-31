package com.qiushu.websocket.client;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;


public class SocketClient extends WebSocketClient{
    private static final String name = SocketClient.class.getName();
    private static final Logger log = Logger.getLogger(name);

    public SocketClient(String url) throws URISyntaxException {
        super(new URI(url));
    }


    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        log.info("websocket客户端和服务器连接成功");
    }

    @Override
    public void onMessage(String s) {
        log.info("websocket客户端收到消息="+ s);
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        log.info("websocket客户端退出连接");
    }

    @Override
    public void onError(Exception e) {
        log.info("websocket客户端和服务器连接发生错误="+ e.getMessage());
    }
}
