package com.brawl.logic.message.home;

import com.brawl.logic.message.PiranhaMessage;
import com.brawl.server.network.ClientConnection;

public class SeasonRewardsMessage extends PiranhaMessage {

    public SeasonRewardsMessage() {
        super(50);
    }

    @Override
    public void encode(ClientConnection clientConnection) {
        var stream = this.getByteStream();

        stream.writeVInt(4);

        stream.writeVInt(15); // rewards count

        for (int j = 0; j < 15; j++) {
            stream.writeVInt(j + 1); // win count to get it
            stream.writeVInt(1); // collected state
            stream.writeVInt(999); // count
            stream.writeVInt(1);
            stream.writeVInt(0); // reward type (0 - star point, 1 - star token)
            stream.writeBoolean(false);
        }

        stream.writeBoolean(false); // has gem offer, unused in v29?
    }

    @Override
    public int getMessageType() {
        return 24123;
    }

}
