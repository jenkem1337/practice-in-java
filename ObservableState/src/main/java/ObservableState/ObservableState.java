package ObservableState;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
public class ObservableState<T> implements Subject{

    private final List<Observer> observers;
    private final BiFunction<Record, T, T> reducer;
    private T state;
    public ObservableState(T initialState, BiFunction<Record, T, T> reducer){
        state = initialState;
        observers = new ArrayList<>();
        this.reducer = reducer;
    }
    @Override
    public void attachObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void detachObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public T getState() {
        return state;
    }

    @Override
    public void setState(Record command) {
        this.state = reducer.apply(command, this.state);
        observers.forEach(observer -> observer.onStateChanged(this));
    }
}
