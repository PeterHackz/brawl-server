package com.brawl.logic.message.admin;

import com.brawl.logic.message.PiranhaMessage;
import com.brawl.server.network.ClientConnection;

public class GiftCodeMessage extends PiranhaMessage {

    private String url;

    public GiftCodeMessage(String url) {
        super(16);
        this.url = url;
    }

    @Override
    public void encode(ClientConnection clientConnection) {
        var stream = this.getByteStream();
        stream.writeString(url);
    }

    @Override
    public int getMessageType() {
        return 20666;
    }

}
