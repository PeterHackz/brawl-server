package com.brawl.logic.message;

import com.brawl.logic.datastream.ByteStream;
import com.brawl.server.network.ClientConnection;
import com.brawl.titan.Messaging.State;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PiranhaMessage {

    private ByteStream byteStream;

    public PiranhaMessage(int size) {
        byteStream = new ByteStream(size);
    }

    public PiranhaMessage(ByteStream byteStream) {
        this.byteStream = byteStream;
    }

    public PiranhaMessage() {
    }

    public State getRequiredState() {
        return State.AUTH_FAILED;
    }

    public int getMessageType() {
        return -1;
    }

    public int getMessageVersion() {
        return 0;
    }

    public void encode(ClientConnection clientConnection) {
    }

    public void decode() throws Exception {
    }

    public void process(ClientConnection clientConnection) throws Exception {
    }

}
