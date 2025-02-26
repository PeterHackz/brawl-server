package com.brawl.logic.message.admin;

import com.brawl.logic.message.PiranhaMessage;
import com.brawl.server.network.ClientConnection;
import com.brawl.titan.Messaging.State;

public class AskForStatsMessage extends PiranhaMessage {

    public static class NotAdminException extends Exception {
        public NotAdminException() {
            super("not an admin to send this message!");
        }
    }

    @Override
    public void process(ClientConnection clientConnection) throws Exception {

        if (!clientConnection.getLogicPlayer().isAdmin())
            throw new NotAdminException();

        clientConnection.getMessageManager().sendMessage(new StatsDataMessage());
    }

    @Override
    public State getRequiredState() {
        return State.HOME;
    }

}
