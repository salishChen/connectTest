package com.qiushu.netty.server;

import cn.hutool.core.thread.ThreadUtil;
import com.qiushu.netty.server.tool.NettyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@Component
public class nettyConfig {

    private Channel serverChannel;
    private static final int DEFAULT_PORT = 16239;
    //bossGroup只是处理连接请求
    private static EventLoopGroup bossGroup = null;
    //workGroup处理非连接请求，如果牵扯到数据量处理业务非常耗时的可以再单独新建一个eventLoopGroup,并在childHandler初始化的时候添加到pipeline绑定
    private static EventLoopGroup workGroup = null;

    /**
     * 启动Netty服务
     *
     * @return 启动结果
     */
    //PostConstruct 注释用于在依赖关系注入完成之后需要执行的方法上，以执行任何初始化
    //1、只有一个非静态方法能使用此注解
    //2、被注解的方法不得有任何参数
    //3、被注解的方法返回值必须为void
    //4、被注解方法不得抛出已检查异常
    //5、此方法只会被执行一次

//    @PostConstruct
    public boolean start() {
        bossGroup = new NioEventLoopGroup();
        workGroup = new NioEventLoopGroup();
        //创建服务端启动对象
        ServerBootstrap bootstrap = new ServerBootstrap();
        try {
            //使用链式编程来设置
            bootstrap.group(bossGroup, workGroup)//设置两个线程组
                    //使用NioSocketChannel作为服务器的通道实现
                    .channel(NioServerSocketChannel.class)
                    //设置线程队列得到的连接数
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    //设置保持活动连接状态
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //设置处理器  WorkerGroup 的 EvenLoop 对应的管道设置处理器
                    .childHandler(new ChannelInitializer<Channel>() {

                        @Override
                        protected void initChannel(Channel ch){
                            log.info("--------------有客户端连接");
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new StringEncoder());
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    });
            //绑定端口, 同步等待成功;
            ChannelFuture future = bootstrap.bind(DEFAULT_PORT).sync();
            log.info("netty服务启动成功，ip：{}，端口：{}", InetAddress.getLocalHost().getHostAddress(), DEFAULT_PORT);
            serverChannel = future.channel();
            ThreadUtil.execute(() -> {
                //等待服务端监听端口关闭
                try {
                    future.channel().closeFuture().sync();
                    log.info("netty服务正常关闭成功，ip：{}，端口：{}", InetAddress.getLocalHost().getHostAddress(), DEFAULT_PORT);
                } catch (InterruptedException | UnknownHostException e) {
                    e.printStackTrace();
                } finally {
                    shutdown();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            log.error("netty服务异常，异常原因：{}", e.getMessage());
            return false;
        }
        return true;
    }


    /**
     * 关闭当前server
     */
    public boolean close() {
        if (serverChannel != null) {
            serverChannel.close();//关闭服务
            try {
                //保险起见
                serverChannel.closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            } finally {
                shutdown();
                serverChannel = null;
            }
        }
        return true;
    }

    /**
     * 优雅关闭
     */
    private void shutdown() {
        workGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }

}
