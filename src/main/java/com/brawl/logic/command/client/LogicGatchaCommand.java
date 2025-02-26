package com.brawl.logic.command.client;

import com.brawl.logic.LogicPlayer;
import com.brawl.logic.command.LogicCommand;
import com.brawl.logic.datastream.ByteStream;
import com.brawl.server.network.ClientConnection;
import com.brawl.titan.Messaging.State;

@SuppressWarnings("unused")
public class LogicGatchaCommand extends LogicCommand {

    private int type;

    @Override
    public void decode(ByteStream stream) {
        type = stream.readVInt();
    }

    @Override
    public void execute(ClientConnection clientConnection) throws Exception {
        LogicPlayer player = clientConnection.getLogicPlayer();
    }

    @Override
    public State getRequiredState() {
        return State.HOME;
    }

}
