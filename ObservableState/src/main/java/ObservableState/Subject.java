package ObservableState;

import java.util.function.Consumer;

public interface Subject {
    void attachObserver(Observer observer);
    void detachObserver(Observer observer);
    Object getState();
    void setState(Record state);
}
