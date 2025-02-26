package com.brawl.logic.message.team;

import com.brawl.logic.message.PiranhaMessage;
import com.brawl.server.network.ClientConnection;

public class TeamLeftMessage extends PiranhaMessage {

    public TeamLeftMessage() {
        super(4);
    }

    @Override
    public void encode(ClientConnection clientConnection) {
        this.getByteStream().writeInt(0);
    }

    @Override
    public int getMessageType() {
        return 24125;
    }

}
