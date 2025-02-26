package com.brawl.logic.message.home;

import com.brawl.logic.command.LogicCommand;
import com.brawl.logic.message.PiranhaMessage;
import com.brawl.server.network.ClientConnection;

public class AvailableServerCommandMessage extends PiranhaMessage {

    private LogicCommand command;

    public AvailableServerCommandMessage(LogicCommand command) {
        super(250);
        this.command = command;
    }

    @Override
    public void encode(ClientConnection clientConnection) {
        var stream = this.getByteStream();

        stream.writeVInt(command.getCommandType());
        command.encode(stream);

    }

    @Override
    public int getMessageType() {
        return 24111;
    }

}
