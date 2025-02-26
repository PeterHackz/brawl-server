package com.brawl.logic.message.team;

import com.brawl.logic.message.PiranhaMessage;
import com.brawl.logic.message.team.TeamMessage.NotInATeamException;
import com.brawl.server.network.ClientConnection;
import com.brawl.titan.Messaging.State;

public class TeamLeaveMessage extends PiranhaMessage {

    @Override
    public void process(ClientConnection clientConnection) throws Exception {
        var player = clientConnection.getLogicPlayer();

        if (!player.isInTeam())
            throw new NotInATeamException();

        player.setSelectedLocation(null);
        clientConnection.getMessageManager().sendMessage(new TeamLeftMessage());
    }

    @Override
    public State getRequiredState() {
        return State.HOME;
    }

}
