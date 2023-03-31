package com.qiushu.websocket.server;


import com.alibaba.fastjson.JSONObject;
import com.qiushu.util.spring.SpringRedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.io.IOException;
import java.util.Random;

public class MyThread implements Runnable {
    private String name;
    @Autowired
    @Qualifier("MyThread")
    private RedisTemplate redisTemplate;

    private boolean stopMe = true;

    public void stopMe() {
        stopMe = false;
    }

    public void setName(String name)

    {
        this.name = name;
    }
    @Override
    public void run() {
        WebSocket webSocket = new WebSocket();
        int num = 0;
        ValueOperations valueOperations = redisTemplate.opsForValue();
        while (valueOperations.get(name)!=null) {
            try {
                JSONObject data = new JSONObject();
                Random r = new Random();
                data.put("test",r.nextInt());
                webSocket.sendMessageByUserId(name,data.toJSONString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(500);
                num++;
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
