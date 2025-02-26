package com.brawl.logic.message.account;

import com.brawl.logic.message.PiranhaMessage;
import com.brawl.server.network.ClientConnection;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class LoginFailedMessage extends PiranhaMessage {

    public static final class ErrorCodes {
        public static final int CustomError = 1, Update = 8;
    }

    private int errorCode;

    private int maintenanceTime;
    private String message;
    private String updateUrl;
    private String masterHash;
    private String redirectUrl;
    private String contentServer;

    public LoginFailedMessage() {
        super(400);
    }

    @Override
    public void encode(ClientConnection clientConnection) {
        var stream = this.getByteStream();

        stream.writeInt(errorCode);

        stream.writeString(masterHash);

        stream.writeString(redirectUrl);

        stream.writeString(contentServer);

        stream.writeString(updateUrl);

        stream.writeString(message);

        stream.writeInt(maintenanceTime);

        stream.writeString();
        stream.writeString();

        stream.writeInt(0);
        stream.writeInt(3);

        stream.writeString();
        stream.writeString();

        stream.writeInt(0);
        stream.writeInt(0);

        stream.writeVInt(0);
        stream.writeVInt(0);
    }

    @Override
    public int getMessageType() {
        return 20103;
    }

}
