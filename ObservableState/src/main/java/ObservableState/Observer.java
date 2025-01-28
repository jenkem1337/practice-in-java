package ObservableState;

import java.util.function.Consumer;
import java.util.function.Function;

public interface Observer {
    void onStateChanged(Subject subject);
}
