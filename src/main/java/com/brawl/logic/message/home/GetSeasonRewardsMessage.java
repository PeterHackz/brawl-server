package com.brawl.logic.message.home;

import com.brawl.logic.message.PiranhaMessage;
import com.brawl.server.network.ClientConnection;
import com.brawl.titan.Messaging.State;

public class GetSeasonRewardsMessage extends PiranhaMessage {

    @Override
    public void decode() throws Exception {
        System.out.println(this.getByteStream().readVInt());
    }

    @Override
    public void process(ClientConnection clientConnection) throws Exception {
        clientConnection.getMessageManager().sendMessage(new SeasonRewardsMessage());
    }

    @Override
    public State getRequiredState() {
        return State.HOME;
    }

}
