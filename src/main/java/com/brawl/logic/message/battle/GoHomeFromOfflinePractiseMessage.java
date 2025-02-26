package com.brawl.logic.message.battle;

import com.brawl.logic.LogicPlayer;
import com.brawl.logic.battle.LogicBattleState;
import com.brawl.logic.battle.LogicBattleType;
import com.brawl.logic.message.PiranhaMessage;
import com.brawl.logic.message.home.OwnHomeDataMessage;
import com.brawl.server.network.ClientConnection;
import com.brawl.titan.Messaging.State;

public class GoHomeFromOfflinePractiseMessage extends PiranhaMessage {

    public static class NotInBattleScreenException extends Exception {
        public NotInBattleScreenException() {
            super("GoHomeFromOfflinePractiseMessage: player is not in battle screen!");
        }
    }

    @Override
    public void process(ClientConnection clientConnection) throws Exception {
        LogicPlayer player = clientConnection.getLogicPlayer();

        if (player.getBattleState() != LogicBattleState.SCREEN && player.getBattleType() == LogicBattleType.MULTIPLAYER)
            throw new NotInBattleScreenException();
        player.setBattleState(LogicBattleState.NONE);

        clientConnection
                .setState(State.HOME)
                .getMessageManager()
                .sendMessage(new OwnHomeDataMessage());
    }

    @Override
    public State getRequiredState() {
        return State.BATTLE;
    }

}
