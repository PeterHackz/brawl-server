package com.brawl.logic.message.avatar;

import com.brawl.logic.command.server.LogicChangeAvatarNameCommand;
import com.brawl.logic.message.PiranhaMessage;
import com.brawl.logic.message.home.AvailableServerCommandMessage;
import com.brawl.server.network.ClientConnection;
import com.brawl.titan.Messaging.State;

public class ChangeAvatarNameMessage extends PiranhaMessage {

    private String name;

    @Override
    public void decode() throws Exception {
        name = getByteStream().readString();
    }

    @Override
    public void process(ClientConnection clientConnection) throws Exception {
        if (name.length() < 3 || name.contains("<c") || name.length() > 20) {
            clientConnection.getMessageManager().sendMessage(new AvatarNameChangeFailedMessage());
            return;
        }
        clientConnection.getLogicPlayer().setName(name);
        clientConnection.getMessageManager().sendMessage(
                new AvailableServerCommandMessage(new LogicChangeAvatarNameCommand(clientConnection.getLogicPlayer())));
    }

    @Override
    public State getRequiredState() {
        return State.HOME;
    }

}
