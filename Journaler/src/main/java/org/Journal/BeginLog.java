package org.Journal;

public record BeginLog(Integer systemChangeNumber) {
    @Override
    public String toString() {
        return "BEGIN,"+systemChangeNumber+"\n";
    }
}
