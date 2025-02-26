package com.brawl.server.network;

import com.brawl.logic.debug.Debugger;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.ReadTimeoutException;
import lombok.extern.java.Log;

import java.util.HashMap;
import java.util.logging.Level;

@Log
public class Connection extends ChannelInboundHandlerAdapter {

    private static final HashMap<String, Integer> map = new HashMap<>();
    private final ClientConnection clientConnection;
    private Channel ch;

    public Connection() {
        clientConnection = new ClientConnection(this);
    }

    public Channel getChannel() {
        return ch;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ch = ctx.channel();
        TCPServer.getConnections().incrementAndGet();
        clientConnection.onOpen();
        String ip = clientConnection.getIp();
        synchronized (map) {
            int count = map.getOrDefault(ip, 0);
            map.put(ip, count + 1);
            if (count + 1 > 100) {
                Debugger.warn("ip %s could be a ddos, closing connection till queue count drop.", ip);
                ctx.close();
                return;
            }
        }
        Debugger.info("Connection.channelActive -> %s connected", clientConnection.getAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        ctx.close();
        String ip = clientConnection.getIp();
        synchronized (map) {
            int count = map.get(ip);
            if (count == 1) {
                map.remove(ip);
            } else {
                map.put(ip, count - 1);
            }
        }
        Debugger.info("Connection.channelInactive -> %s disconnected", clientConnection.getAddress());
        clientConnection.onClose();
        TCPServer.getConnections().decrementAndGet();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buffer = (ByteBuf) msg;
        byte[] bytes = new byte[buffer.writerIndex()];
        buffer.readBytes(bytes);
        buffer.release();
        clientConnection.getMessageManager().getMessaging().enqueue(bytes);
        clientConnection.update();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable ex) {
        if (!(ex instanceof java.net.SocketException)
                && !(ex instanceof ReadTimeoutException))
            log.log(Level.SEVERE, ex.getMessage(), ex);
        ctx.close();
    }

}
