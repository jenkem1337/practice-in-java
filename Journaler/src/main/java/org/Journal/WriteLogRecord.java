package org.Journal;

public record WriteLogRecord(
        Integer scn,
        LogCommand command,
        String key,
        String newValue,
        String oldValue) {

    @Override
    public String toString() {
        return "WRITE" + "," +
                scn() + "," +
                command().name() + "," +
                key() + "," +
                newValue() + "," +
                oldValue() + "\n";
    }
}
