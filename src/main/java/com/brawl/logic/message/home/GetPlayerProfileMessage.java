package com.brawl.logic.message.home;

import com.brawl.logic.LogicPlayer;
import com.brawl.logic.datastream.ByteStream;
import com.brawl.logic.message.PiranhaMessage;
import com.brawl.logic.message.account.LoginMessage.InvalidCredentialsException;
import com.brawl.server.Cache;
import com.brawl.server.network.ClientConnection;
import com.brawl.titan.Messaging.State;

public class GetPlayerProfileMessage extends PiranhaMessage {

    private int hi, lo;

    @Override
    public void decode() throws Exception {
        ByteStream stream = this.getByteStream();
        hi = stream.readInt();
        lo = stream.readInt();
    }

    @Override
    public void process(ClientConnection clientConnection) throws Exception {
        LogicPlayer player = Cache.getPlayer(hi, lo);

        if (player == null)
            throw new InvalidCredentialsException();

        clientConnection.getMessageManager().sendMessage(new PlayerProfileMessage(player));
    }

    @Override
    public State getRequiredState() {
        return State.HOME;
    }

}
