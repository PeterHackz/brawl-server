package com.brawl.logic.home;

import java.util.ArrayList;

import com.brawl.logic.datastream.ByteStream;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogicQuests {

    private ArrayList<QuestData> quests;

    public LogicQuests() {
        quests = new ArrayList<>();
    }

    public void encode(ByteStream stream) {
        stream.writeVInt(quests.size());
        for (QuestData quest : quests)
            quest.encode(stream);
    }

}
