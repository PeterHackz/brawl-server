package com.brawl.logic.command.client;

import com.brawl.logic.LogicPlayer;
import com.brawl.logic.command.LogicCommand;
import com.brawl.logic.data.LogicCharacterData;
import com.brawl.logic.datastream.ByteStream;
import com.brawl.logic.home.LogicHeroData;
import com.brawl.server.network.ClientConnection;
import com.brawl.titan.Messaging.State;

public class LogicLevelUpCommand extends LogicCommand {

    private static final Exception HERO_LOCKED_EXCEPTION = new Exception("trying to upgrade a locked hero!");

    private LogicCharacterData characterData;

    @Override
    public void decode(ByteStream stream) {
        characterData = stream.readDataReference();
    }

    @Override
    public void execute(ClientConnection clientConnection) throws Exception {
        LogicHeroData heroData = clientConnection.getLogicPlayer().getHero(characterData.getDataId());
        if (heroData == null)
            throw HERO_LOCKED_EXCEPTION;

        int currentLevel = heroData.getLevel();
        if (heroData.getPowerPoints() < LogicHeroData.LEVEL_POINTS[currentLevel])
            throw new Exception("no enough power points to upgrade hero!");

        LogicPlayer player = clientConnection.getLogicPlayer();
        if (player.getGold() < LogicHeroData.UPGRADE_COST[currentLevel])
            throw new Exception("no enough gold to upgrade hero!");

        player.setGold(player.getGold() - LogicHeroData.UPGRADE_COST[currentLevel]);
        player.setHeroLevel(heroData, currentLevel + 1);
    }

    @Override
    public State getRequiredState() {
        return State.HOME;
    }

}
