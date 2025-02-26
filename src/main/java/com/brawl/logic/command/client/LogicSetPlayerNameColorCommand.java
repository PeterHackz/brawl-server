package com.brawl.logic.command.client;

import com.brawl.logic.command.LogicCommand;
import com.brawl.logic.data.LogicNameColorData;
import com.brawl.logic.datastream.ByteStream;
import com.brawl.server.network.ClientConnection;
import com.brawl.titan.Messaging.State;

public class LogicSetPlayerNameColorCommand extends LogicCommand {

    public static class InvalidNameColorException extends Exception {
        public InvalidNameColorException() {
            super("invalid name color!");
        }
    }

    private LogicNameColorData nameColorData;

    @Override
    public void decode(ByteStream stream) {
        nameColorData = stream.readDataReference();
    }

    @Override
    public void execute(ClientConnection clientConnection) throws Exception {

        if (nameColorData == null)
            throw new InvalidNameColorException();
            
        clientConnection.getLogicPlayer().setSelectedNameColor(nameColorData);
    }

    @Override
    public State getRequiredState() {
        return State.HOME;
    }

}
