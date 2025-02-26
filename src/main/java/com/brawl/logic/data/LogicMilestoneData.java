package com.brawl.logic.data;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class LogicMilestoneData extends LogicData {

    private int type, index, progressStart, progress, season, primaryLvlUpRewardType, primaryLvlUpRewardCount;
    private String primaryLvlUpRewardData;

    private int secondaryLvlUpRewardType, secondaryLvlUpRewardCount;

    private String secondaryLvlUpRewardData;
    private int dependsOnIndex;

    public boolean doesDependOnIndex() {
        return dependsOnIndex > 0;
    }

}
