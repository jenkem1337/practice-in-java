package org.Journal;

public record CommitLog(Integer systemChangeNumber) {
    @Override
    public String toString() {
        return "COMMIT,"+systemChangeNumber+"\n";
    }
}
