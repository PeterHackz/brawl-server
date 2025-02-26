package com.brawl.logic.home;

import com.brawl.logic.data.LogicData;
import com.brawl.logic.home.LogicGoals.RewardType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogicMilestoneReward {

    private LogicMilestoneReward secondaryReward;
    private int requiredProgress, index;
    private LogicData data;
    private RewardType rewardType;
    private int count;

}
