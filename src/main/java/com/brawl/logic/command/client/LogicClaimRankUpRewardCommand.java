package com.brawl.logic.command.client;

import java.util.HashMap;

import com.brawl.logic.LogicPlayer;
import com.brawl.logic.command.LogicCommand;
import com.brawl.logic.command.server.LogicGiveDeliveryItemsCommand;
import com.brawl.logic.home.DeliveryItem;
import com.brawl.logic.home.DeliveryUnit;
import com.brawl.logic.csv.LogicDataTables;
import com.brawl.logic.data.LogicCharacterData;
import com.brawl.logic.data.LogicSkinData;
import com.brawl.logic.datastream.ByteStream;
import com.brawl.logic.home.GatchaDrop;
import com.brawl.logic.home.LogicMilestoneReward;
import com.brawl.logic.home.LogicGoals;
import com.brawl.logic.home.LogicHeroData;
import com.brawl.logic.home.GatchaDrop.BoxType;
import com.brawl.logic.home.LogicGoals.MilestoneType;
import com.brawl.logic.home.LogicGoals.RewardType;
import com.brawl.logic.math.LogicBitList;
import com.brawl.logic.message.home.AvailableServerCommandMessage;
import com.brawl.server.ServerConfiguration;
import com.brawl.server.network.ClientConnection;
import com.brawl.titan.Messaging.State;

public class LogicClaimRankUpRewardCommand extends LogicCommand {

    public static class InvalidMilestoneException extends Exception {
        public InvalidMilestoneException() {
            super("invalid milestone type!");
        }
    }

    public static class UnsupportedRewardTypeException extends Exception {
        public UnsupportedRewardTypeException() {
            super("unsupported reward type!");
        }
    }

    private int milestoneType, idx;

    private LogicCharacterData characterData;

    @Override
    public void decode(ByteStream stream) {
        milestoneType = stream.readVInt();
        characterData = stream.<LogicCharacterData>readDataReference(); // used when collecting power points reward
        stream.readVInt();
        idx = stream.readVInt();
    }

    public static interface RewardHandler {
        public DeliveryUnit[] run(LogicPlayer player, LogicMilestoneReward reward, LogicCharacterData characterData)
                throws Exception;
    }

    private static HashMap<RewardType, RewardHandler> handlers = new HashMap<>() {
        {
            put(RewardType.GOLD, (player, reward, characterData) -> {
                DeliveryUnit deliveryUnit = new DeliveryUnit();
                deliveryUnit.setType(DeliveryUnit.GOLD);
                deliveryUnit.setCount(reward.getCount());
                player.setGold(player.getGold() + reward.getCount());
                return new DeliveryUnit[] { deliveryUnit };
            });
            put(RewardType.DIAMOND, (player, reward, characterData) -> {
                DeliveryUnit deliveryUnit = new DeliveryUnit();
                deliveryUnit.setType(DeliveryUnit.DIAMOND);
                deliveryUnit.setCount(reward.getCount());
                player.setDiamonds(player.getDiamonds() + reward.getCount());
                return new DeliveryUnit[] { deliveryUnit };
            });
            put(RewardType.SKIN, (player, reward, characterData) -> {
                DeliveryUnit deliveryUnit = new DeliveryUnit();
                deliveryUnit.setType(DeliveryUnit.SKIN);
                deliveryUnit.setCount(reward.getCount());
                deliveryUnit.setData(reward.getData());
                player.unlockSkin((LogicSkinData) reward.getData());
                LogicCharacterData character = ((LogicSkinData) reward.getData()).getSkinConfsData()
                        .getCharacterData();
                if (player.getHero(character.getDataId()) == null) {
                    player.unlockHero(character);
                    return new DeliveryUnit[] {
                            new DeliveryUnit().setType(DeliveryUnit.CHARACTER).setCount(1).setData(character),
                            deliveryUnit };
                } else
                    return new DeliveryUnit[] { deliveryUnit };
            });
            put(RewardType.CHARACTER, (player, reward, characterData) -> {
                DeliveryUnit deliveryUnit = new DeliveryUnit();
                deliveryUnit.setType(DeliveryUnit.CHARACTER);
                deliveryUnit.setCount(reward.getCount());
                deliveryUnit.setData(reward.getData());
                player.unlockHero((LogicCharacterData) reward.getData());
                return new DeliveryUnit[] { deliveryUnit };
            });
            put(RewardType.PIN, (player, reward, characterData) -> {
                DeliveryUnit deliveryUnit = new DeliveryUnit();
                deliveryUnit.setType(DeliveryUnit.EMOTE);
                deliveryUnit.setCount(reward.getCount());
                deliveryUnit.setData(reward.getData());
                System.out.println("TODO");
                return new DeliveryUnit[] { deliveryUnit };
            });
            put(RewardType.POWER_POINT, (player, reward, characterData) -> {
                LogicHeroData hero = player.getHero(characterData.getDataId());
                if (hero.getPowerPoints() == LogicHeroData.getMaxedPoints()) {
                    throw new Exception("trying to add power points to a locked hero!");
                }
                DeliveryUnit deliveryUnit = new DeliveryUnit();
                deliveryUnit.setType(DeliveryUnit.POWER_POINT);
                int pps = reward.getCount();
                int loss = 0;
                if (pps + hero.getPowerPoints() > LogicHeroData.getMaxedPoints()) {
                    int newPps = LogicHeroData.getMaxedPoints() - hero.getPowerPoints();
                    loss = pps - newPps;
                    pps = newPps;
                }
                deliveryUnit.setCount(pps);
                deliveryUnit.setData(characterData);
                player.setHeroPowerPoints(hero, hero.getPowerPoints() + pps);
                if (loss == 0)
                    return new DeliveryUnit[] { deliveryUnit };
                player.setGold(player.getGold() + (loss * 2));
                return new DeliveryUnit[] { deliveryUnit,
                        new DeliveryUnit().setType(DeliveryUnit.GOLD).setCount(loss * 2) };
            });
            put(RewardType.PIN_PACK, (player, reward, characterData) -> {

                DeliveryUnit pin1 = new DeliveryUnit();
                pin1.setType(DeliveryUnit.EMOTE);
                pin1.setCount(1);
                pin1.setData(LogicDataTables.getDataById(LogicDataTables.EMOTES, 300));

                DeliveryUnit pin2 = new DeliveryUnit();
                pin2.setType(DeliveryUnit.EMOTE);
                pin2.setCount(1);
                pin2.setData(LogicDataTables.getDataById(LogicDataTables.EMOTES, 306));

                DeliveryUnit pin3 = new DeliveryUnit();
                pin3.setType(DeliveryUnit.EMOTE);
                pin3.setCount(1);
                pin3.setData(LogicDataTables.getDataById(LogicDataTables.EMOTES, 301));

                return new DeliveryUnit[] { pin1, pin2, pin3 };
            });
        }
    };

    @Override
    public void execute(ClientConnection clientConnection) throws Exception {
        LogicMilestoneReward milestoneReward = null;
        if (milestoneType == MilestoneType.PREMIUM_PASS)
            milestoneReward = LogicGoals.getPremiumPassRewards()[idx];
        else if (milestoneType == MilestoneType.FREE_PASS)
            milestoneReward = LogicGoals.getFreePassRewards()[idx];
        else if (milestoneType == MilestoneType.TROPHY_ROAD)
            milestoneReward = LogicGoals.getTrophyRoadRewards()[idx];
        else
            throw new InvalidMilestoneException();

        LogicPlayer player = clientConnection.getLogicPlayer();

        if (characterData != null && player.getHero(characterData.getDataId()) == null) {
            throw new Exception("trying to add power points to a locked hero!");
        }

        if (milestoneType == MilestoneType.TROPHY_ROAD) {
            int collectedRewards = player.getTrophyRoadCollectedRewards();
            if (idx != collectedRewards) {
                throw new Exception("collecting a locked reward!?");
            }
            player.setTrophyRoadCollectedRewards(collectedRewards + 1);
        } else {
            if (milestoneReward.getRequiredProgress() > player.getTokens())
                throw new Exception("attempting to claim a locked reward!");

            LogicBitList bitList;
            if (milestoneType == MilestoneType.PREMIUM_PASS) {
                if (!player.isPremiumBrawlPassBought())
                    throw new Exception("attempting to claim a premium brawl pass reward when brawl pass is locked!");
                bitList = player.getPremiumBrawlPass();
                player.markPremPassRewardsModified();
            } else {
                bitList = player.getFreeBrawlPass();
                player.markFreePassRewardsModified();
            }
            System.out.println("reward: " + milestoneReward.getRequiredProgress());
            if (bitList.isTrue(idx + 2)) {
                throw new Exception("brawl pass reward is already collected!");
            }
            bitList.setTrue(idx + 2);
        }

        if (milestoneReward.getRewardType() == RewardType.CHARACTER) {
            if (player.getHero(milestoneReward.getData().getDataId()) != null) {
                milestoneReward = milestoneReward.getSecondaryReward();
            }
        }

        RewardType rewardType = milestoneReward.getRewardType();

        DeliveryUnit[] deliveryUnits;

        if (rewardType == RewardType.SMALL_BOX || rewardType == RewardType.MEDIUM_BOX
                || rewardType == RewardType.BIG_BOX) {
            deliveryUnits = GatchaDrop.create(
                    rewardType == RewardType.SMALL_BOX ? BoxType.SMALL
                            : (rewardType == RewardType.MEDIUM_BOX ? BoxType.MEDIUM : BoxType.BIG),
                    player).toArray(new DeliveryUnit[0]);
        } else {
            RewardHandler rewardHandler = handlers.get(rewardType);
            if (rewardHandler == null)
                throw new UnsupportedRewardTypeException();
            deliveryUnits = rewardHandler.run(player, milestoneReward,
                    characterData);
        }

        clientConnection.getMessageManager()
                .sendMessage(new AvailableServerCommandMessage(
                        new LogicGiveDeliveryItemsCommand(rewardType == RewardType.SMALL_BOX ? 10
                                : (rewardType == RewardType.MEDIUM_BOX ? 12
                                        : (rewardType == RewardType.BIG_BOX ? 11 : 100)),
                                milestoneType,
                                milestoneType == MilestoneType.TROPHY_ROAD ? 0 : ServerConfiguration.BRAWLPASS_SEASON,
                                idx,
                                new DeliveryItem(deliveryUnits))));
    }

    @Override
    public State getRequiredState() {
        return State.HOME;
    }

}
