package com.qiushu.websocket.server;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/websocket/{name}")
public class WebSocket {

    @Autowired
    private RedisTemplate redisTemplate;

    MyThread thread1=new MyThread();
    //与某个客户端连接对话，通过此对客户端发送消息
    private Session session;

    //存放所有连接的客户端
    private static final ConcurrentHashMap<String, WebSocket> webSocketConcurrentHashMap = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam(value = "name") String name) throws IOException {

        //默认客户端，没有重名
        this.session = session;
        webSocketConcurrentHashMap.put(name, this);
        System.out.println(name + "连接webSocket成功");
        System.out.println("当前连接人数为：" + webSocketConcurrentHashMap.size());
        redisTemplate.opsForValue().set(name, "true");
        createThread("admin1","1");
    }

    @OnClose
    public void onClose() {

        thread1.stopMe();

        //这里判断不周，仅实验用
        for (String name : webSocketConcurrentHashMap.keySet()) {
            redisTemplate.delete(name);
            if (this == webSocketConcurrentHashMap.get(name)) {

                webSocketConcurrentHashMap.remove(name);
                break;
            }
        }


        System.out.println("【webSocket退出成功】当前连接人数为：" + webSocketConcurrentHashMap.size());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {

        System.out.println("error:");
        throwable.printStackTrace();
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException {


        //此处可以指定发送，或者群发，或者xxxx的

        System.out.println("【webSocket接收成功】内容为：" + message);
        if (message.indexOf("name:") == 0) {
            String name = message.substring(message.indexOf("name") + 5, message.indexOf(";"));
            //获取sender的Stirng
            for (String senderStr : webSocketConcurrentHashMap.keySet()) {
                if (webSocketConcurrentHashMap.get(senderStr).getSession() == session) {
                    String message1 = message.substring(message.indexOf(";") + 1);
//                        appointSending(senderStr, name, message1);

                    if(webSocketConcurrentHashMap.get(name)!=null){
                        webSocketConcurrentHashMap.get(name).sendMessage(message1);
                    }
                }
            }
        } else {
//                groupSending(message, session);
            sendInfo(message);
        }

    }

    //群发
    public void groupSending(String message, Session exIncludeSession) {

        for (String name : webSocketConcurrentHashMap.keySet()) {

            try {
                if (exIncludeSession == webSocketConcurrentHashMap.get(name).session)
                    continue;

                webSocketConcurrentHashMap.get(name).session.getBasicRemote().sendText(name + ":" + message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //指定发
    public void appointSending(String sender, String name, String message) {

        try {
            webSocketConcurrentHashMap.get(name).session.getBasicRemote().sendText(sender + ":" + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过userId向客户端发送消息
     */
    public void sendMessageByUserId(String userId, String message) throws IOException {
        System.out.println("服务端发送消息到" + userId + ",消息：" + message);
        if (StringUtils.isNotBlank(userId) && webSocketConcurrentHashMap.containsKey(userId)) {
            webSocketConcurrentHashMap.get(userId).sendMessage(message);
        } else {
            System.out.println("用户" + userId + "不在线");
        }
    }
    public void createThread(String userId, String message) throws IOException {
        if("admin1".equals(userId)){
            thread1.setName(userId);
            Thread admin1=new Thread(thread1);
            admin1.start();
        } else if("admin2".equals(userId)){
            thread1.setName(userId);
            Thread admin2=new Thread(thread1);
            admin2.start();
        }
        System.out.println("服务端发送消息到" + userId + ",消息：" + message);
//        if (StringUtils.isNotBlank(userId) && webSocketConcurrentHashMap.containsKey(userId)) {
//            webSocketConcurrentHashMap.get(userId).sendMessage(message);
//        } else {
//            System.out.println("用户" + userId + "不在线");
//        }
    }
    /**
     * 向客户端发送消息
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
        //this.session.getAsyncRemote().sendText(message);
    }
    /**
     * 群发自定义消息
     */
    public static void sendInfo(String message) throws IOException {
        for (String item : webSocketConcurrentHashMap.keySet()) {
            try {
                webSocketConcurrentHashMap.get(item).sendMessage(message);
            } catch (IOException e) {
                continue;
            }
        }
    }

    public Session getSession() {
        return session;
    }
}
