package org.Journal;

public record RedoLogRecord(Integer scn, LogCommand command, String key, String newValue, String oldValue) {
}
