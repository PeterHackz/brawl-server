package com.brawl.logic.message.battle;

import com.brawl.logic.LogicPlayer;
import com.brawl.logic.battle.LogicBattleResult;
import com.brawl.logic.battle.LogicBattleState;
import com.brawl.logic.battle.LogicBattleType;
import com.brawl.logic.message.PiranhaMessage;
import com.brawl.server.network.ClientConnection;
import com.brawl.titan.Messaging.State;

public class AskForBattleEndMessage extends PiranhaMessage {

    public static class NotInBattleException extends Exception {
        public NotInBattleException() {
            super("player is not in a battle!");
        }
    }

    public static class MapMismatchException extends Exception {
        public MapMismatchException() {
            super("AskForBattleEndMessage: received map is not the same as the player selected map!");
        }
    }

    private LogicBattleResult battleResult;

    @Override
    public void decode() throws Exception {
        var stream = this.getByteStream();

        int result = stream.readVInt();
        stream.readVInt();
        int specialeventPrecentage = stream.readVInt();
        stream.readVInt();
        battleResult = new LogicBattleResult(result, specialeventPrecentage, stream.readVInt(), stream.readVInt());
        for (int idx = 0; idx < battleResult.getPlayersCount(); idx++)
            battleResult.decodePlayerEntry(stream);

    }

    @Override
    public void process(ClientConnection clientConnection) throws Exception {
        LogicPlayer player = clientConnection.getLogicPlayer();

        if (player.getBattleState() != LogicBattleState.BATTLE || player.getBattleType() == LogicBattleType.TRAINING)
            throw new NotInBattleException();

        player.setBattleState(LogicBattleState.SCREEN);

        if (!player.isInTeam()) {
            if (player.getBattleLocationData() == null)
                throw new NotInBattleException();
            if (!player.getBattleLocationData().equals(battleResult.getLocationData()))
                throw new MapMismatchException();

            player.setBattleLocationData(null);
        }

        clientConnection.getMessageManager().sendMessage(new BattleEndMessage(battleResult));
    }

    @Override
    public State getRequiredState() {
        return State.BATTLE;
    }

}
