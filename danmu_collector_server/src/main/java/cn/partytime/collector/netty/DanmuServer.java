package cn.partytime.collector.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;


/**
 * Created by user on 16/6/22.
 */

@Component
public class DanmuServer {

    private static final Logger logger = LoggerFactory.getLogger(DanmuServer.class);

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;


    @Value("${netty.port:9090}")
    private int port;

    @Autowired
    private DanmuServerInitializer danmuServerInitializer;

    /**
     * 启动系统加载项目
     */
    @PostConstruct
    public void init(){
        nettyStart();
    }

    private void nettyStart(){
        logger.info("netty服务启动!");
        try{
            bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup();
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(danmuServerInitializer)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            // 绑定端口，开始接收进来的连接
            ChannelFuture f = serverBootstrap.bind(port).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    private void stop(){
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }

}
