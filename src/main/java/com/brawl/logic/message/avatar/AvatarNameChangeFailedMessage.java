package com.brawl.logic.message.avatar;

import com.brawl.logic.message.PiranhaMessage;
import com.brawl.server.network.ClientConnection;

public class AvatarNameChangeFailedMessage extends PiranhaMessage {

    public AvatarNameChangeFailedMessage() {
        super(1);
    }

    @Override
    public void encode(ClientConnection clientConnection) {
        getByteStream().writeVInt(0);
    }

    @Override
    public int getMessageType() {
        return 20205;
    }

}
