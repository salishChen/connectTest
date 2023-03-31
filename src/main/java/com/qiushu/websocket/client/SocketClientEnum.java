package com.qiushu.websocket.client;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import org.java_websocket.WebSocket;

public enum  SocketClientEnum {
    CLIENT;

    private static SocketClient socketClient = null;

    public static void initClient(SocketClient client) {
        socketClient = client;
        if(ObjectUtils.isNotNull(socketClient)) {
            socketClient.connect();
            while (!client.getReadyState().equals(WebSocket.READYSTATE.OPEN)){
                System.out.println("还没有打开");
            }
            System.out.println("打开了");
            socketClient.send("测试websocket。。。");
        }
        boolean flag = true;
        int i=3;
        while(flag) {
            socketClient.send("测试websocket。。。"+(i--));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if(i == 0) {
                flag = false;
            }
        }
    }
}
