package org.Journal;

public record UndoLogRecord(Integer scn, LogCommand command, String key, String oldValue, String newValue) {
    public UndoLogRecord(Integer scn, LogCommand command, String key, String oldValue, String newValue) {
        this.scn = scn;
        this.command = (command == LogCommand.INSERT) ? LogCommand.DELETE : LogCommand.INSERT;
        this.key = key;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
}
