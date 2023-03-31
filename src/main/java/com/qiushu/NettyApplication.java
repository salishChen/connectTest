package com.qiushu;

import com.qiushu.netty.client.ChannelManager;
import com.qiushu.netty.client.NettyClient;
import com.qiushu.websocket.client.SocketClient;
import com.qiushu.websocket.client.SocketClientEnum;
import io.netty.channel.Channel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class NettyApplication {
    public static void main(String[] args)throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(NettyApplication.class, args);

        //netty服务端
        //启动netty服务端需去 nettyConfig 启用PostConstruct注解

        //netty客户端
        //启动netty客户端需去 NettyClient 启用PostConstruct注解 并启动下列代码
        /*Channel abc = NettyClient.createChannel();
        ChannelManager.putChannel("abc",abc);
        ChannelManager.sendData("abc","123123:12");*/

        //websocket服务端
        //启动websocket服务端需去 WebSocketConfig 启用Bean注解

        //websocket客户端
        //启动websocket客户端仅需解除此行注解↓
        //SocketClientEnum.CLIENT.initClient(new SocketClient("ws://192.168.151.145:9099/websocket/admin1"));

    }
}
