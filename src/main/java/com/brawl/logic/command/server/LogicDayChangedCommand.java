package com.brawl.logic.command.server;

import com.brawl.logic.LogicPlayer;
import com.brawl.logic.command.LogicCommand;
import com.brawl.logic.datastream.ByteStream;
import com.brawl.logic.home.LogicConfData;

import lombok.Setter;

@Setter
public class LogicDayChangedCommand extends LogicCommand {

    private LogicPlayer player;

    private boolean shouldEncodeConfData;

    public LogicDayChangedCommand() {
        shouldEncodeConfData = false;
    }

    public LogicDayChangedCommand(LogicPlayer player) {
        this.player = player;
        this.shouldEncodeConfData = true;
    }

    @Override
    public void encode(ByteStream stream) {
        stream.writeBoolean(shouldEncodeConfData);
        if (shouldEncodeConfData)
            LogicConfData.encode(stream, player);
    }

    @Override
    public int getCommandType() {
        return 204;
    }

}
