package org.Journal;

public record AbortLog (Integer systemChangedNumber){
    @Override
    public String toString() {
        return "ABORT,"+systemChangedNumber +"\n";
    }
}
