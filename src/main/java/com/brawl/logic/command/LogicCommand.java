package com.brawl.logic.command;

import com.brawl.logic.datastream.ByteStream;
import com.brawl.server.network.ClientConnection;
import com.brawl.titan.Messaging.State;

public abstract class LogicCommand {

    public void decode(ByteStream stream) {
    }

    public void encode(ByteStream stream) {
    }

    public void execute(ClientConnection clientConnection) throws Exception {
    }

    public int getCommandType() {
        return -1;
    }

    public State getRequiredState() {
        return State.AUTH_FAILED;
    }

}
