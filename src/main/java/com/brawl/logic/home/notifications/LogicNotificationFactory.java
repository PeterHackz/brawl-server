package com.brawl.logic.home.notifications;

import java.security.MessageDigest;
import java.util.ArrayList;

import org.bson.Document;

import com.brawl.logic.csv.LogicDataTables;
import com.brawl.logic.datastream.ByteStream;
import com.brawl.logic.debug.Debugger;
import com.brawl.logic.utils.HTTPSConnection;
import com.brawl.server.ServerConfiguration;

public class LogicNotificationFactory {

    public static String PROMO_POPUP_HASH;

    public static final String PROMO_POPUP_ASSETS_URL = ServerConfiguration.MB_SITE + "/images/promo/";

    public static final String PROMO_POPUP_IMAGE_NAME = "basicbgr.png";

    public static final String PROMO_POPUP_IMAGE_URL = PROMO_POPUP_ASSETS_URL + PROMO_POPUP_IMAGE_NAME;

    public static void init() {
        if (ServerConfiguration.IS_LOCAL_DEBUG_BUILD)
            return;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest(HTTPSConnection.downloadFile(PROMO_POPUP_IMAGE_URL));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xFF & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            PROMO_POPUP_HASH = hexString.toString();
            Debugger.info("Computed promo popup hash: %s", PROMO_POPUP_HASH);
        } catch (Exception e) {
            Debugger.warn("failed to compute promo popup hash, defaulting to null");
        }
    }

    public static final BaseNotification fromBson(Document doc) {
        BaseNotification baseNotification = null;
        switch (doc.getInteger("type")) {
            case HeroRewardNotification.NOTIFICATION_TYPE:
                int character = doc.getInteger("cd", -1);
                baseNotification = new HeroRewardNotification(character == -1 ? null
                        : LogicDataTables
                                .getDataByGlobalId(character));
                break;
            case SkinRewardNotification.NOTIFICATION_TYPE:
                int skin = doc.getInteger("sd", -1);
                baseNotification = new SkinRewardNotification(skin == -1 ? null
                        : LogicDataTables
                                .getDataByGlobalId(skin));
                break;
            case GemRewardNotification.NOTIFICATION_TYPE:
                baseNotification = new GemRewardNotification(doc.getInteger("a"));
                break;
            case ResourceRewardNotification.NOTIFICATION_TYPE:
                baseNotification = new ResourceRewardNotification(
                        doc.getInteger("rt"), doc.getInteger("a"));
                break;
            case BrawlPassRewardNotification.NOTIFICATION_TYPE:
                baseNotification = new BrawlPassRewardNotification();
                break;
            case IAPDeliveryNotification.NOTIFICATION_TYPE:
                baseNotification = new IAPDeliveryNotification(doc.getInteger("a"));
                break;
            case FreeTextNotification.NOTIFICATION_TYPE:
                baseNotification = new FreeTextNotification(null);
                break;
        }

        if (baseNotification != null) {
            baseNotification.setNotificationTime(doc.getLong("nt"));
            baseNotification.setNotificationRead(doc.getBoolean("nrd"));
            baseNotification.setMessage(doc.getString("msg"));
            baseNotification.setNotificationId(doc.getInteger("nid"));
        }

        return baseNotification;
    }

    public void encode(ArrayList<BaseNotification> notifications, ByteStream stream) {
        stream.writeVInt(notifications.size());
        for (BaseNotification notification : notifications) {
            stream.writeVInt(notification.getNotificationType());
            stream.writeInt(1);
            notification.encode(stream);
        }
    }

}
