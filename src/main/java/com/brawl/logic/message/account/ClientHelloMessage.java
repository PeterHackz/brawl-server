package com.brawl.logic.message.account;

import com.brawl.logic.message.PiranhaMessage;
import com.brawl.server.network.ClientConnection;
import com.brawl.titan.Messaging.State;

@SuppressWarnings("unused")
public class ClientHelloMessage extends PiranhaMessage {

    @Override
    public void decode() throws Exception {
        var stream = this.getByteStream();

        stream.readInt();
        stream.readInt();

        int majorVersion = stream.readInt();
    }

    @Override
    public void process(ClientConnection clientConnection) throws Exception {
        clientConnection.setState(State.PEPPER_LOGIN);
        clientConnection.getMessageManager().sendMessage(new ServerHelloMessage());
    }

    @Override
    public State getRequiredState() {
        return State.PEPPER_AUTH;
    }

}
