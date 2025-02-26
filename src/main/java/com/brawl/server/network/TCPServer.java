package com.brawl.server.network;

import com.brawl.logic.debug.Debugger;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.Getter;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPServer {

    @Getter
    private static final AtomicInteger connections = new AtomicInteger();
    @Getter
    private static ChannelFuture f;
    @Getter
    private static EventLoopGroup bossGroup;
    @Getter
    private static EventLoopGroup workerGroup;

    public static void init() throws Exception {
        Logger.getLogger("io.netty").setLevel(Level.OFF);
    }

    public static void start(int port) throws Exception {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup(8);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_REUSEADDR, true);
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(
                            new ChannelInitializer<SocketChannel>() {
                                @Override
                                public void initChannel(SocketChannel ch) throws Exception {
                                    ch.pipeline().addLast("readTimeoutHandler", new ReadTimeoutHandler(30));
                                    ch.pipeline().addLast(new Connection());
                                }
                            });
            Debugger.info("TCPServer: ServerBootstrap binded on: 0.0.0.0:%d", port);
            f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            shutdown();
        }
    }

    public static void start() throws Exception {
        start(9339);
    }

    public static void shutdown() {
        // this shutdowns the whole program
        try {
            TCPServer.getBossGroup().shutdownGracefully().sync();
            TCPServer.getWorkerGroup().shutdownGracefully().sync();
            TCPServer.getF().channel().closeFuture().sync();
        } catch (InterruptedException ignored) {
        }
        System.exit(0);
    }

}
