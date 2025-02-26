package com.brawl.logic.message.home;

import com.brawl.logic.LogicPlayer;
import com.brawl.logic.battle.LogicBattleType;
import com.brawl.logic.home.LogicHeroData;
import com.brawl.logic.message.PiranhaMessage;
import com.brawl.server.network.ClientConnection;

public class StartBattleMessage extends PiranhaMessage {

    public StartBattleMessage() {
        super(20);
    }

    @Override
    public void encode(ClientConnection clientConnection) {
        LogicPlayer player = clientConnection.getLogicPlayer();
        LogicHeroData hero = player.getSelectedHero();
        LogicBattleType battleType = player.getBattleType();

        var stream = this.getByteStream();

        stream.writeByte(
                battleType == LogicBattleType.MULTIPLAYER ? 0 : (battleType == LogicBattleType.FRIENDLY ? 1 : 2));
        if (battleType != LogicBattleType.MULTIPLAYER) {
            stream.writeByte(0); // use skin
            stream.writeInt(hero.getSelectedSkin().getGlobalId());
            stream.writeInt(hero.getCharacterData().getGlobalId());
            stream.writeInt(player.getSelectedLocation().getGlobalId());
        }
    }

    @Override
    public int getMessageType() {
        return 24130;
    }

}
