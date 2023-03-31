package com.qiushu.netty.client;

import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;

public class ChannelManager {


    /**
     * 在线会话(存储注册成功的会话)
     */
    private static final ConcurrentHashMap<String, Channel> onlineChannels = new ConcurrentHashMap<>();


    /**
     * 加入
     *
     * @param mn
     * @param channel
     * @return
     */
    public static boolean putChannel(String mn, Channel channel) {
        if (!onlineChannels.containsKey(mn)) {
            boolean success = onlineChannels.putIfAbsent(mn, channel) == null ? true : false;
            return success;
        }
        return false;
    }

    /**
     * 移除
     *
     * @param mn
     */
    public static Channel removeChannel(String mn) {
        return onlineChannels.remove(mn);
    }

    /**
     * 获取Channel
     *
     * @param mn
     * @return
     */
    public static Channel getChannel(String mn) {
        // 获取一个可用的会话
        Channel channel = onlineChannels.get(mn);
        if (channel != null) {
            // 连接有可能是断开，加入已经断开连接了，我们需要进行尝试重连
            if (!channel.isActive()) {
                //先移除之前的连接
                removeChannel(mn);
                return null;
            }
        }
        return channel;
    }

    /**
     * 发送消息[自定义协议]
     *
     * @param <T>
     * @param mn
     * @param msg
     */
    public static <T> void sendMessage(String mn, String msg) {
        Channel channel = onlineChannels.get(mn);
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(msg);
        }
    }

    /**
     * 发送消息[自定义协议]
     *
     * @param <T>
     * @param msg
     */
    public static <T> void sendChannelMessage(Channel channel, String msg) {
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(msg);
        }
    }

    /**
     * 关闭连接
     *
     * @return
     */
    public static void closeChannel(String mn) {
        onlineChannels.get(mn).close();
    }

    /**
     * 发送数据包
     * @param key
     */
    public static void sendData(String key, String data) {
        Channel channel = ChannelManager.getChannel(key);
        //将通道存入
        if(channel==null){
            NettyClient client = new NettyClient();
            channel = client.createChannel();
            ChannelManager.putChannel(key, channel);
        }
        if (channel != null && channel.isActive()) {
            //发送数据
            channel.writeAndFlush(data);
            System.out.println("-------------转发数据成功-------------");
        }
    }
}
