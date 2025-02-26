package com.brawl.logic.command.server;

import com.brawl.logic.LogicPlayer;
import com.brawl.logic.command.LogicCommand;
import com.brawl.logic.datastream.ByteStream;

import lombok.Setter;

@Setter
public class LogicKeyPoolChangedCommand extends LogicCommand {

    private LogicPlayer player;

    public LogicKeyPoolChangedCommand(LogicPlayer player) {
        this.player = player;
    }

    @Override
    public void encode(ByteStream stream) {
        stream.writeVInt(player.getAvailableBattleTokens());
        stream.writeVInt((player.getNextBattleTokensReset() != -1 || player.getAvailableBattleTokens() == 200)
                ? (int) ((player.getNextBattleTokensReset() - System.currentTimeMillis()) / 1000)
                : 0);
    }

    @Override
    public int getCommandType() {
        return 209;
    }

}
