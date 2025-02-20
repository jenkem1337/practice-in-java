package org.Journal;

public record CheckpointLog(String type, String systemChangeNumbers) {
    @Override
    public String toString() {
        return type + systemChangeNumbers+"\n";
    }
}
