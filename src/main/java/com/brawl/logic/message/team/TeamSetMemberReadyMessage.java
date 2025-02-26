package com.brawl.logic.message.team;

import com.brawl.logic.battle.LogicBattleType;
import com.brawl.logic.message.PiranhaMessage;
import com.brawl.logic.message.home.StartBattleMessage;
import com.brawl.server.network.ClientConnection;
import com.brawl.titan.Messaging.State;

public class TeamSetMemberReadyMessage extends PiranhaMessage {

    @Override
    public void process(ClientConnection clientConnection) throws Exception {
        clientConnection.getLogicPlayer().setBattleType(LogicBattleType.FRIENDLY);
        clientConnection.getMessageManager().sendMessage(new StartBattleMessage());
        clientConnection.setState(State.BATTLE);
    }

    @Override
    public State getRequiredState() {
        return State.HOME;
    }

}
