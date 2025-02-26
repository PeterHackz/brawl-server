package com.brawl.logic.home;

import lombok.Getter;

@Getter
public class DeliveryItem {

    private DeliveryUnit[] deliveryUnits;

    public DeliveryItem(DeliveryUnit... deliveryUnits) {
        this.deliveryUnits = deliveryUnits;
    }

}
