package org.Journal;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface Journal {
    Integer beginTransaction() throws IOException;
    void commit(Integer systemChangeNumber)throws IOException;
    void rollback(Integer systemChangeNumber) throws IOException;
    void write(Integer systemChangeNumber, LogCommand command, String key, String newValue, String oldValue) throws IOException;
    void recovery() throws IOException;
}
