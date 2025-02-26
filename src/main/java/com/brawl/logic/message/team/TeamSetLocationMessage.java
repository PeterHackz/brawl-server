package com.brawl.logic.message.team;

import com.brawl.logic.data.LogicLocationData;
import com.brawl.logic.message.PiranhaMessage;
import com.brawl.logic.message.team.TeamMessage.NotInATeamException;
import com.brawl.server.network.ClientConnection;
import com.brawl.titan.Messaging.State;

public class TeamSetLocationMessage extends PiranhaMessage {

    public static class InvalidLocationException extends Exception {
        public InvalidLocationException() {
            super("TeamSetLocationMessage: invalid location!");
        }
    }

    private LogicLocationData locationData;

    @Override
    public void decode() throws Exception {
        var stream = this.getByteStream();

        locationData = stream.<LogicLocationData>readDataReference();
    }

    @Override
    public void process(ClientConnection clientConnection) throws Exception {

        if (!clientConnection.getLogicPlayer().isInTeam())
            throw new NotInATeamException();

        else if (locationData != null)
            clientConnection.getLogicPlayer().setSelectedLocation(locationData);
        else
            throw new InvalidLocationException();

        clientConnection.getMessageManager().sendMessage(new TeamMessage());
    }

    @Override
    public State getRequiredState() {
        return State.HOME;
    }

}
