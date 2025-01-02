package edu.kingston.domain.user.command;

import java.util.List;
import java.util.Stack;

public class CommandManager {
    private Stack<Command> commandHistory = new Stack<>();

    public void executeCommand(Command command) {
        command.execute();
        commandHistory.push(command);
    }

    public Command undoLastCommand() {
        Command command = null;
        if (!commandHistory.isEmpty()) {
            command = commandHistory.pop();
            command.undo();
        }
        return command;
    }

    public void clearHistory() {
        commandHistory.clear();
    }

    public List<String> getActionLog() {
        return commandHistory.stream()
                .map(Command::getDescription)
                .toList();
    }

}

