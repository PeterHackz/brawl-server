package com.brawl.logic.command.client;

import com.brawl.logic.LogicPlayer;
import com.brawl.logic.command.LogicCommand;
import com.brawl.logic.datastream.ByteStream;
import com.brawl.logic.home.LogicGoals;
import com.brawl.server.ServerConfiguration;
import com.brawl.server.network.ClientConnection;
import com.brawl.titan.Messaging.State;

public class LogicPurchaseBrawlPassCommand extends LogicCommand {

    private boolean isPurchasingBundle;

    @Override
    public void decode(ByteStream stream) {
        stream.readVInt(); // season id
        isPurchasingBundle = stream.readBoolean();
    }

    @Override
    public void execute(ClientConnection clientConnection) throws Exception {
        int cost = isPurchasingBundle ? ServerConfiguration.BRAWL_PASS_BUNDLE_COST : ServerConfiguration.BRAWLPASS_COST;
        LogicPlayer player = clientConnection.getLogicPlayer();

        if (player.getDiamonds() < cost)
            throw new Exception("no enough gems to buy brawl pass!");

        player.setDiamonds(player.getDiamonds() - cost);
        player.setPremiumBrawlPassBought(true);

        if (isPurchasingBundle) {
            int maxTokens = LogicGoals.getLastTierRequiredProgress();
            int newTokens = Math.min(player.getTokens() + ServerConfiguration.BRAWLPASS_BUNDLE_TOKENS, maxTokens);
            player.setTokens(newTokens);
        }
    }

    @Override
    public State getRequiredState() {
        return State.HOME;
    }

}
