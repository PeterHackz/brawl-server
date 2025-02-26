package com.brawl.titan;

import com.brawl.logic.debug.Debugger;
import com.brawl.server.network.ClientConnection;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class Messaging {

    public static enum State {
        LOGIN_FAILED,
        AUTH_FAILED,
        PEPPER_AUTH,
        PEPPER_LOGIN,
        HOME,
        BATTLE,
        HOME_OR_BATTLE,
    }

    public static void send(ClientConnection clientConnection, int type, int version, byte[] buffer) {
        byte[] payload = clientConnection.getPepperCrypto().encrypt(type, buffer);
        ByteBuf message = Unpooled.buffer(payload.length + 7);
        message.writeShort(type);
        message.writeMedium(payload.length);
        message.writeShort(version);
        message.writeBytes(payload);
        final ChannelFuture future = clientConnection.getConnection().getChannel().writeAndFlush(message);
        if (Debugger.isVerbose()) {
            future.addListener(
                    new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) {
                            Debugger.info(
                                    "Messaging<%s>.send -> type<%d> length<%d> version<%d>",
                                    clientConnection.getAddress(), type, payload.length, version);
                        }
                    });
        }
    }

    // messaging Queue
    /*
     * why?
     * netty do not ways receive full message
     * so we wait till we have a full message in the queue
     * then process it.
     */
    private byte[] queue;

    private int offset;

    public Messaging() {
        queue = new byte[3000];
    }

    public void drainQueues() {
        queue = null;
        offset = 0;
    }

    public void enqueue(byte[] bytes) {
        System.arraycopy(bytes, 0, queue, offset, bytes.length);
        offset += bytes.length;
    }

    public byte[] nextMessage() {
        return dequeue();
    }

    public byte[] onReceive(ClientConnection clientConnection, int type, byte[] payload)
            throws Exception {

        if (type == 10100)
            clientConnection.setPepperCrypto(new PepperCrypto());

        return clientConnection.getPepperCrypto().decrypt(type, payload);
    }

    private byte[] dequeue() {
        byte[] result;
        if (offset < 7)
            return null; // header is 7 bytes
        int length = (((queue[2] & 0xFF) << 16) | ((queue[3] & 0xFF) << 8) | (queue[4] & 0xFF));
        if (length <= offset - 7) {
            result = new byte[length + 7];
            System.arraycopy(queue, 0, result, 0, length + 7);
            System.arraycopy(queue, 7 + length, queue, 0, (queue.length - 7 - length));
            offset -= (7 + length);
            return result;
        } else {
            return null;
        }
    }
}
