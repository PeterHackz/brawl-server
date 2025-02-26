package com.brawl.logic.command.client;

import com.brawl.logic.LogicEventsManager;
import com.brawl.logic.LogicPlayer;
import com.brawl.logic.command.LogicCommand;
import com.brawl.logic.datastream.ByteStream;
import com.brawl.server.network.ClientConnection;
import com.brawl.titan.Messaging.State;

import java.util.HashMap;

public class LogicClaimDailyRewardCommand extends LogicCommand {

    private int eventIdx;

    @Override
    public void decode(ByteStream stream) {
        eventIdx = stream.readVInt();
        stream.readVInt();
    }

    @Override
    public void execute(ClientConnection clientConnection) throws Exception {
        LogicPlayer player = clientConnection.getLogicPlayer();
        HashMap<Integer, Integer> events = player.getEventsSeen();
        int changeTime = LogicEventsManager.getEventByID(eventIdx).changeTime();
        if (events.getOrDefault(eventIdx, 0) == changeTime) {
            throw new Exception("trying to reclaim event reward!");
        }
        events.put(eventIdx, changeTime);
        player.setTokens(player.getTokens() + 10);
        player.markChangeInEventsSeen();
    }

    @Override
    public State getRequiredState() {
        return State.HOME;
    }

}
