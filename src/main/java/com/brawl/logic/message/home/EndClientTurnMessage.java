package com.brawl.logic.message.home;

import com.brawl.logic.command.LogicCommand;
import com.brawl.logic.command.LogicCommandFactory;
import com.brawl.logic.message.PiranhaMessage;
import com.brawl.server.network.ClientConnection;
import com.brawl.titan.Messaging.State;

public class EndClientTurnMessage extends PiranhaMessage {

    private LogicCommand[] commands;

    @Override
    public void decode() throws Exception {
        var stream = this.getByteStream();

        stream.readVInt();
        stream.readVInt();
        stream.readVInt();

        int count = stream.readVInt();

        if (count > 50) { // why would client send 50 commands?
            throw new AlotOfCommandsException();
        }

        commands = new LogicCommand[count];

        LogicCommand command;

        for (int i = 0; i < count; i++) {
            if ((command = readCommand()) == null) {
                return;
            }
            commands[i] = command;
        }
    }

    @Override
    public void process(ClientConnection clientConnection) throws Exception {
        for (LogicCommand command : commands) {

            if (command == null)
                break;

            if (command.getRequiredState() != clientConnection.getState())
                throw new InvalidStateForCommandException();

            command.execute(clientConnection);
        }
    }

    @Override
    public State getRequiredState() {
        return State.HOME_OR_BATTLE;
    }

    private LogicCommand readCommand() {
        var stream = this.getByteStream();

        int commandType = stream.readVInt();

        LogicCommand command = LogicCommandFactory.createCommandByType(commandType);

        if (command == null)
            return null;

        stream.readVInt();
        stream.readVInt();
        stream.readVInt();
        stream.readVInt();

        command.decode(stream);

        return command;
    }

    public static class AlotOfCommandsException extends Exception {
        public AlotOfCommandsException() {
            super("EndClientTurnMessage: alot of commands were received in one message!");
        }
    }

    public static class InvalidStateForCommandException extends Exception {
        public InvalidStateForCommandException() {
            super("EndClientTurnMessage: received command in invalid state!");
        }
    }

}
