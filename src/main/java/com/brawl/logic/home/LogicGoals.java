package com.brawl.logic.home;

import java.util.ArrayList;

import com.brawl.logic.csv.LogicDataTables;
import com.brawl.logic.data.LogicData;
import com.brawl.logic.data.LogicMilestoneData;
import com.brawl.server.ServerConfiguration;

import lombok.Getter;

public class LogicGoals {

    public static enum RewardType {
        GOLD,
        DIAMOND,
        CHARACTER,
        SKIN,
        PIN,
        PIN_PACK,
        SMALL_BOX,
        MEDIUM_BOX,
        BIG_BOX,
        POWER_POINT,
        GAMEMODE,
        TOKEN_DOUBLER,
        QUESTS
    }

    private static RewardType getRewardTypeByIntType(int type) {
        return switch (type) {
            case 1 -> RewardType.GOLD;
            case 3 -> RewardType.CHARACTER;
            case 4, 24 -> RewardType.SKIN;
            case 6 -> RewardType.SMALL_BOX;
            case 9 -> RewardType.TOKEN_DOUBLER;
            case 10 -> RewardType.BIG_BOX;
            case 12 -> RewardType.POWER_POINT;
            case 13 -> RewardType.GAMEMODE;
            case 14 -> RewardType.MEDIUM_BOX;
            case 16 -> RewardType.DIAMOND;
            case 18 -> RewardType.QUESTS;
            case 19 -> RewardType.PIN;
            case 21 -> RewardType.PIN_PACK;
            default -> null;
        };
    }

    private static LogicMilestoneReward[] PREMIUM_REWARDS, FREE_REWARDS, TROPHY_ROAD_REWARDS;

    @Getter
    private static int lastTierRequiredProgress = 0;

    public static final class MilestoneType {
        public static final int TROPHY_ROAD = 6,
                PREMIUM_PASS = 9,
                FREE_PASS = 10;
    }

    private static void addDataToReward(LogicMilestoneReward milestoneReward, String dataName) {
        LogicData data = switch (milestoneReward.getRewardType()) {
            case CHARACTER -> LogicDataTables.getCharacterDataByName(dataName);
            case SKIN -> LogicDataTables.getSkinDataByName(dataName);
            default -> null;
        };
        milestoneReward.setData(data);
    }

    public static void init() {
        LogicData[] milestones = LogicDataTables.getDataTable(LogicDataTables.MILESTONES);

        ArrayList<LogicMilestoneReward> premiumPassRewards = new ArrayList<>();
        ArrayList<LogicMilestoneReward> freePassRewards = new ArrayList<>();
        ArrayList<LogicMilestoneReward> trophyRoadRewards = new ArrayList<>();

        for (LogicData data : milestones) {
            LogicMilestoneData milestoneData = ((LogicMilestoneData) data);

            if (milestoneData.getType() != MilestoneType.TROPHY_ROAD
                    && ((milestoneData.getType() != MilestoneType.PREMIUM_PASS
                            && milestoneData.getType() != MilestoneType.FREE_PASS)
                            || milestoneData.getSeason() != ServerConfiguration.BRAWLPASS_SEASON))
                continue;

            LogicMilestoneReward reward = new LogicMilestoneReward();
            RewardType rewardType = getRewardTypeByIntType(milestoneData.getPrimaryLvlUpRewardType());
            reward.setRewardType(rewardType);
            reward.setCount(milestoneData.getPrimaryLvlUpRewardCount());
            reward.setIndex(milestoneData.getIndex());
            reward.setRequiredProgress(milestoneData.getProgressStart() + milestoneData.getProgress());
            addDataToReward(reward, milestoneData.getPrimaryLvlUpRewardData());

            if (milestoneData.getSecondaryLvlUpRewardType() != -1) {
                LogicMilestoneReward secondaryReward = new LogicMilestoneReward();
                RewardType rewardType2 = getRewardTypeByIntType(milestoneData.getSecondaryLvlUpRewardType());
                secondaryReward.setRewardType(rewardType2);
                secondaryReward.setCount(milestoneData.getSecondaryLvlUpRewardCount());
                addDataToReward(secondaryReward, milestoneData.getSecondaryLvlUpRewardData());
                reward.setSecondaryReward(secondaryReward);
            }

            if (milestoneData.getType() == MilestoneType.PREMIUM_PASS)
                premiumPassRewards.add(reward);
            else if (milestoneData.getType() == MilestoneType.FREE_PASS)
                freePassRewards.add(reward);
            else if (milestoneData.getType() == MilestoneType.TROPHY_ROAD)
                trophyRoadRewards.add(reward);
        }

        PREMIUM_REWARDS = premiumPassRewards.toArray(new LogicMilestoneReward[0]);
        FREE_REWARDS = freePassRewards.toArray(new LogicMilestoneReward[0]);
        TROPHY_ROAD_REWARDS = trophyRoadRewards.toArray(new LogicMilestoneReward[0]);

        lastTierRequiredProgress = FREE_REWARDS[FREE_REWARDS.length - 1].getRequiredProgress();

        LogicDataTables.deleteTable(LogicDataTables.MILESTONES);
    }

    public static LogicMilestoneReward[] getPremiumPassRewards() {
        return PREMIUM_REWARDS;
    }

    public static LogicMilestoneReward[] getFreePassRewards() {
        return FREE_REWARDS;
    }

    public static LogicMilestoneReward getNextTier(int tokens) {
        for (LogicMilestoneReward milestoneReward : FREE_REWARDS)
            if (milestoneReward.getRequiredProgress() > tokens)
                return milestoneReward;
        return null;
    }

    public static LogicMilestoneReward[] getTrophyRoadRewards() {
        return TROPHY_ROAD_REWARDS;
    }

}
