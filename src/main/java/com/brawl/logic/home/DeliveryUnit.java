package com.brawl.logic.home;

import com.brawl.logic.csv.LogicDataTables;
import com.brawl.logic.data.LogicData;
import com.brawl.logic.datastream.ByteStream;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class DeliveryUnit {

    public static final int CHARACTER = 1,
            TOKEN_DOUBLER = 2,
            TICKET = 3,
            POWER_POINT = 6,
            GOLD = 7,
            DIAMOND = 8,
            SKIN = 9,
            EMOTE = 11;

    private int type, count;

    private LogicData data;

    public void encode(ByteStream stream) {
        stream.writeVInt(count);
        stream.writeDataReference(data != null && data.getClassId() == LogicDataTables.CHARACTERS ? data : null);
        stream.writeVInt(type);
        stream.writeDataReference(data != null && data.getClassId() == LogicDataTables.SKINS ? data : null);
        stream.writeDataReference(data != null && data.getClassId() == LogicDataTables.EMOTES ? data : null);
        stream.writeDataReference(data != null && data.getClassId() == LogicDataTables.CARDS ? data : null);
        stream.writeVInt(0);
    }
}
