package com.brawl.logic.message.account;

import com.brawl.logic.message.PiranhaMessage;
import com.brawl.server.network.ClientConnection;
import com.brawl.titan.Messaging.State;

public class KeepAliveMessage extends PiranhaMessage {

    @Override
    public void process(ClientConnection clientConnection) throws Exception {
        clientConnection.getMessageManager().sendMessage(new KeepAliveServerMessage());
    }

    @Override
    public State getRequiredState() {
        return State.HOME_OR_BATTLE;
    }

}
