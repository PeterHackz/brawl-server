package com.brawl.logic.command.client;

import com.brawl.logic.LogicPlayer;
import com.brawl.logic.command.LogicCommand;
import com.brawl.logic.data.LogicSkinData;
import com.brawl.logic.datastream.ByteStream;
import com.brawl.logic.home.LogicHeroData;
import com.brawl.server.network.ClientConnection;
import com.brawl.titan.Messaging.State;

public class LogicSelectSkinCommand extends LogicCommand {

    private LogicSkinData skinData;

    @Override
    public void decode(ByteStream stream) {
        skinData = stream.readDataReference();
    }

    @Override
    public void execute(ClientConnection clientConnection) throws Exception {

        if (skinData == null)
            throw new InvalidSkinException();

        LogicPlayer player = clientConnection.getLogicPlayer();
        LogicHeroData heroData = player.getHero(skinData.getCharacterData().getDataId());
        if (heroData == null)
            throw new LockedCharacterException();

        if (!heroData.getCharacterData().getDefaultSkinData().equals(skinData)
                && player.isSkinUnlocked(skinData.getDataId()))
            throw new LockedSkinException();

        player.setSelectedSkin(heroData, skinData)
                .setSelectedHero(heroData);

    }

    @Override
    public State getRequiredState() {
        return State.HOME;
    }

    public static class InvalidSkinException extends Exception {
        public InvalidSkinException() {
            super("invalid skin!");
        }
    }

    public static class LockedCharacterException extends Exception {
        public LockedCharacterException() {
            super("locked character!");
        }
    }

    public static class LockedSkinException extends Exception {
        public LockedSkinException() {
            super("locked skin!");
        }
    }

}
