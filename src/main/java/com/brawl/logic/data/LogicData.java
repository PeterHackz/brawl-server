package com.brawl.logic.data;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public abstract class LogicData {

    private int classId, dataId;

    public int getGlobalId() {
        return classId * 1000000 + dataId;
    }

}
