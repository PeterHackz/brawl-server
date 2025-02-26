package com.brawl.logic.message.home;

import com.brawl.logic.battle.LogicBattleState;
import com.brawl.logic.battle.LogicBattleType;
import com.brawl.logic.message.PiranhaMessage;
import com.brawl.server.network.ClientConnection;
import com.brawl.titan.Messaging.State;

public class StartTrainingMessage extends PiranhaMessage {

    @Override
    public void decode() {
    }

    @Override
    public void process(ClientConnection clientConnection) throws Exception {
        clientConnection.getLogicPlayer()
                .setBattleType(LogicBattleType.TRAINING)
                .setBattleState(LogicBattleState.BATTLE);
        clientConnection.setState(State.BATTLE);
    }

    @Override
    public State getRequiredState() {
        return State.HOME;
    }

}
