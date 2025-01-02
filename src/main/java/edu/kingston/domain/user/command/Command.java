package edu.kingston.domain.user.command;

// Command Pattern for User Actions
public interface Command {
    void execute();
    void undo();
    String getDescription();
}