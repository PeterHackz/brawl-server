package com.brawl.logic.command.server;

import com.brawl.logic.LogicPlayer;
import com.brawl.logic.command.LogicCommand;
import com.brawl.logic.datastream.ByteStream;

public class LogicChangeAvatarNameCommand extends LogicCommand {

    private LogicPlayer player;

    public LogicChangeAvatarNameCommand(LogicPlayer player) {
        this.player = player;
    }

    @Override
    public void encode(ByteStream stream) {
        stream.writeString(player.getName());
        stream.writeVInt(0);
    }

    @Override
    public int getCommandType() {
        return 201;
    }

}
