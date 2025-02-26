package com.brawl.logic.message.battle;

import com.brawl.logic.message.PiranhaMessage;
import com.brawl.server.network.ClientConnection;

public class UdpConnectionInfoMessage extends PiranhaMessage {

    public UdpConnectionInfoMessage() {
        super(30);
    }

    @Override
    public void encode(ClientConnection clientConnection) {
        var stream = this.getByteStream();
        // hardcoded, I am testing.
        stream.writeVInt(20500);
        stream.writeString("192.168.1.105");
        stream.writeInt(10);
        byte[] sessionId = new byte[10];
        for (byte i = 0; i < 10; i++)
            sessionId[i] = i;
        stream.writeBytes(sessionId);
        byte[] c = new byte[80];
        for (byte i = 0; i < 80; i++)
            c[i] = i;
        stream.writeInt(80);
        stream.writeBytes(c);
    }

    @Override
    public int getMessageType() {
        return 24112;
    }

}
