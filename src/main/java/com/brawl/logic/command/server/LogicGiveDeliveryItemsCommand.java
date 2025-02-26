package com.brawl.logic.command.server;

import com.brawl.logic.command.LogicCommand;
import com.brawl.logic.datastream.ByteStream;
import com.brawl.logic.home.DeliveryItem;
import com.brawl.logic.home.DeliveryUnit;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class LogicGiveDeliveryItemsCommand extends LogicCommand {

    private DeliveryItem[] deliveryItems;
    private int type, milestoneType, season, rewardIndex;
    private boolean forcedDrop;

    public LogicGiveDeliveryItemsCommand(int type, int milestoneType, int season, int rewardIndex,
            DeliveryItem... deliveryItems) {
        this.type = type;
        this.deliveryItems = deliveryItems;
        this.milestoneType = milestoneType;
        this.season = season;
        this.rewardIndex = rewardIndex;
    }

    public LogicGiveDeliveryItemsCommand(DeliveryItem... deliveryItems) {
        this.deliveryItems = deliveryItems;
        rewardIndex = -1;
        milestoneType = -1;
    }

    @Override
    public void encode(ByteStream stream) {
        stream.writeVInt(0);
        stream.writeVInt(deliveryItems.length);
        for (DeliveryItem deliveryItem : deliveryItems) {
            stream.writeVInt(type);
            stream.writeVInt(deliveryItem.getDeliveryUnits().length);
            for (DeliveryUnit deliveryUnit : deliveryItem.getDeliveryUnits())
                deliveryUnit.encode(stream);
        }
        stream.writeBoolean(forcedDrop);
        stream.writeVInt(milestoneType == -1 ? 1 : milestoneType);
        stream.writeVInt(rewardIndex == -1 ? 1 : rewardIndex + 2); // index + 2
        stream.writeVInt(rewardIndex == -1 ? 0 : season); // season
        stream.writeVInt(rewardIndex == -1 ? 0 : 1);

        stream.writeVInt(0);
        stream.writeVInt(0);
        stream.writeVInt(0);
        stream.writeVInt(0);
    }

    @Override
    public int getCommandType() {
        return 203;
    }

}
