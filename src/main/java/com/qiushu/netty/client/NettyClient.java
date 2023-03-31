package com.qiushu.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.context.annotation.Configuration;

import org.springframework.stereotype.Component;


import javax.annotation.PostConstruct;

@Configuration
@Component
public class NettyClient {
    private static String ip = "192.168.151.145";
//    private static String ip = "39.105.230.214";

//    private static int port  = 16239;
    private static int port  = 18090;

    /**
     * 服务类
     */
    private static Bootstrap bootstrap=null;

    /**
     * 初始化  项目启动后自动初始化
     */
//    @PostConstruct
    public void init() {

        //worker
        EventLoopGroup worker = new NioEventLoopGroup();

        bootstrap = new Bootstrap();
        //设置线程池
        bootstrap.group(worker);

        //设置socket工厂
        bootstrap.channel(NioSocketChannel.class);

        //设置管道
        bootstrap.handler(new ChannelInitializer<Channel>() {

            @Override
            protected void initChannel(Channel ch) throws Exception {
                ch.pipeline().addLast(new StringDecoder());
                ch.pipeline().addLast(new StringEncoder());
                ch.pipeline().addLast(new ClientHandler());
            }
        });
    }


    /**
     * 获取会话 （获取或者创建一个会话）
     */
    public static Channel createChannel() {
        try {
            Channel channel = bootstrap.connect( ip, port).sync().channel();
            return channel;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
