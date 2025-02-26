package com.brawl.logic.message.team;

import com.brawl.logic.LogicEventsManager;
import com.brawl.logic.message.PiranhaMessage;
import com.brawl.logic.message.team.TeamMessage.AlreadyInATeamException;
import com.brawl.server.network.ClientConnection;
import com.brawl.titan.Messaging.State;

public class TeamCreateMessage extends PiranhaMessage {

    private int eventIdx;

    @Override
    public void decode() throws Exception {
        var stream = this.getByteStream();
        stream.readVInt();
        eventIdx = stream.readVInt();
    }

    @Override
    public void process(ClientConnection clientConnection) throws Exception {
        var player = clientConnection.getLogicPlayer();

        if (player.isInTeam())
            throw new AlreadyInATeamException();

        player.setSelectedLocation(LogicEventsManager.getEvents().get(eventIdx == -1 ? 7 : eventIdx).locationData());
        clientConnection.getMessageManager().sendMessage(new TeamMessage());
    }

    @Override
    public State getRequiredState() {
        return State.HOME;
    }

}
