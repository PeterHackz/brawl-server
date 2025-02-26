package com.brawl.logic.message.account;

import com.brawl.logic.message.PiranhaMessage;
import com.brawl.server.network.ClientConnection;
import com.brawl.titan.TweetNacl;

public class ServerHelloMessage extends PiranhaMessage {

    public ServerHelloMessage() {
        super(28);
    }

    @Override
    public void encode(ClientConnection clientConnection) {
        var stream = this.getByteStream();

        stream.writeInt(24);

        byte[] session_token = new byte[24];
        TweetNacl.randombytes(session_token, 24);

        stream.writeBytes(session_token);
    }

    @Override
    public int getMessageType() {
        return 20100;
    }

}
