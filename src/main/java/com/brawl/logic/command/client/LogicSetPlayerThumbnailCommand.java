package com.brawl.logic.command.client;

import com.brawl.logic.LogicPlayer;
import com.brawl.logic.command.LogicCommand;
import com.brawl.logic.data.LogicPlayerThumbnailData;
import com.brawl.logic.datastream.ByteStream;
import com.brawl.server.network.ClientConnection;
import com.brawl.titan.Messaging.State;

public class LogicSetPlayerThumbnailCommand extends LogicCommand {

    public static class InvalidPLayerThumbnailException extends Exception {
        public InvalidPLayerThumbnailException() {
            super("invalid player thumbnail!");
        }
    }

    public static class RequiredCharacterLockedException extends Exception {
        public RequiredCharacterLockedException() {
            super("required character is locked!");
        }
    }

    public static class RequiredExpLevelNotReachedException extends Exception {
        public RequiredExpLevelNotReachedException() {
            super("required exp level is not reached!");
        }
    }

    public static class RequiredTrophiesNotReachedException extends Exception {
        public RequiredTrophiesNotReachedException() {
            super("required trophies not reached!");
        }
    }

    private LogicPlayerThumbnailData thumbnailData;

    @Override
    public void decode(ByteStream stream) {
        thumbnailData = stream.readDataReference();
    }

    @Override
    public void execute(ClientConnection clientConnection) throws Exception {

        if (thumbnailData == null)
            throw new InvalidPLayerThumbnailException();

        LogicPlayer player = clientConnection.getLogicPlayer();

        if (thumbnailData.requiresCharacter()
                && player.getHero(thumbnailData.getRequiredCharacter().getDataId()) == null)
            throw new RequiredCharacterLockedException();
        if (player.getHighestTrophies() < thumbnailData.getRequiredTotalTrophies())
            throw new RequiredExpLevelNotReachedException();
        if (thumbnailData.getRequiredExpLevel() == 250 && player.getExpPoints() != 321250)
            throw new RequiredTrophiesNotReachedException();

        clientConnection.getLogicPlayer().setSelectedThumbnail(thumbnailData);
    }

    @Override
    public State getRequiredState() {
        return State.HOME;
    }

}
