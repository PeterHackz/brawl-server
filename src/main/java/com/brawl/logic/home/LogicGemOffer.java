package com.brawl.logic.home;

import com.brawl.logic.data.LogicData;
import com.brawl.logic.datastream.ByteStream;

public class LogicGemOffer {

    private int type, multiplier;
    private LogicData data;

    public LogicGemOffer(int type, int multiplier, LogicData data) {
        this.type = type;
        this.multiplier = multiplier;
        this.data = data;
    }

    public void encode(ByteStream stream) {
        stream.writeVInt(type);
        stream.writeVInt(multiplier);
        if (data == null) {
            stream.writeVInt(0);
            stream.writeVInt(0);
        } else {
            stream.writeDataReference(data);
            stream.writeVInt(data.getDataId());
        }
    }

}
