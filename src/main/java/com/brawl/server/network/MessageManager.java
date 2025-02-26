package com.brawl.server.network;

import java.io.IOException;
import java.util.Arrays;

import com.brawl.logic.LogicPlayer;
import com.brawl.logic.command.server.LogicDayChangedCommand;
import com.brawl.logic.datastream.ByteStream;
import com.brawl.logic.debug.Debugger;
import com.brawl.logic.message.LogicLaserMessageFactory;
import com.brawl.logic.message.PiranhaMessage;
import com.brawl.logic.message.home.AvailableServerCommandMessage;
import com.brawl.server.Ticker;
import com.brawl.titan.Messaging;
import com.brawl.titan.Messaging.State;

import lombok.Getter;

@Getter
public class MessageManager {

    public static class PepperIsNullException extends Exception {
        public PepperIsNullException() {
            super("pepper is null!");
        }
    }

    public static class InvalidStateForMessageException extends Exception {
        public InvalidStateForMessageException() {
            super("received message at invalid state");
        }
    }

    private ClientConnection clientConnection;

    private Messaging messaging;

    public MessageManager(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;
        messaging = new Messaging();
    }

    public void update() throws Exception {
        if (clientConnection.getState() == State.HOME
                && clientConnection.getLogicPlayer().getServerTick() != Ticker.getServerTick()) {
            LogicPlayer player = clientConnection.getLogicPlayer();
            sendMessage(new AvailableServerCommandMessage(new LogicDayChangedCommand(player)));
            player.setServerTick(Ticker.getServerTick());
        }
        byte[] message = messaging.nextMessage();
        while (message != null) {
            handleMessage(message);
            message = messaging.nextMessage();
        }
    }

    public void sendMessage(PiranhaMessage message) throws IOException {
        message.encode(clientConnection);
        sendPreEncodedMessage(message);
    }

    public void sendPreEncodedMessage(PiranhaMessage message) throws IOException {
        Messaging.send(clientConnection, message.getMessageType(), message.getMessageVersion(),
                message.getByteStream().getBuffer());
    }

    private void handleMessage(byte[] buffer) throws Exception {
        int messageType = (((buffer[0] & 0xFF) << 8) | (buffer[1] & 0xFF));
        int messageVersion = (((buffer[5] & 0xFF) << 8) | (buffer[6] & 0xFF));
        int messageLength = (((buffer[2] & 0xFF) << 16) | ((buffer[3] & 0xFF) << 8) | (buffer[4] & 0xFF));

        var message = LogicLaserMessageFactory.createMessageByType(messageType);

        Debugger.verbose("MessageManager<%s>.handleMessage -> type<%d> length<%d> version<%d>",
                clientConnection.getAddress(), messageType, messageLength, messageVersion);

        if (messageType != 10100 && clientConnection.getPepperCrypto() == null)
            throw new PepperIsNullException();

        byte[] payload = messaging.onReceive(clientConnection, messageType,
                Arrays.copyOfRange(buffer, 7, 7 + messageLength));

        if (message == null)
            return;
        else if (message.getRequiredState() != clientConnection.getState())
            if (!(message.getRequiredState() == State.HOME_OR_BATTLE && (clientConnection.getState() == State.HOME
                    || clientConnection.getState() == State.BATTLE)))
                throw new InvalidStateForMessageException();

        message.setByteStream(new ByteStream(payload));
        message.decode();
        message.process(clientConnection);
    }

}
