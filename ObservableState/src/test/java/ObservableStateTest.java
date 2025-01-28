import ObservableState.ObservableState;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import ObservableState.Observer;
import ObservableState.Subject;

public class ObservableStateTest {
    private ObservableState<Book> bookState;
    @BeforeEach
    void initState() {
        bookState = new ObservableState<Book>(
                new Book("", 0),
                (command, state) ->
                        switch (command) {
                            case BookNameChanged(String name)      -> new Book(name, state.pageNumber());
                            case PageNumberChanged(int pageNumber) -> new Book(state.name(), pageNumber);
                            default -> throw new IllegalStateException("Invalid command : " + command);
                        }
        );

    }
    @AfterEach
    void afterEach() {
        bookState = null;
    }

    @Test
    public void observableState() {
        assertEquals("", bookState.getState().name());
        assertEquals(0, bookState.getState().pageNumber());
    }

    @Test
    public void setStateWithBookNameChanged() {
        Observer exampleObserver = new Observer() {
            @Override
            public void onStateChanged(Subject subject) {
                var book = (Book) subject.getState();
                assertEquals("Hello World", book.name());
            }
        };

        bookState.attachObserver(exampleObserver);
        bookState.setState(new BookNameChanged("Hello World"));
    }

    @Test
    public void setStateWithPageNumberChanged() {
        Observer exampleObserver = new Observer() {
            @Override
            public void onStateChanged(Subject subject) {
                var book = (Book) subject.getState();
                assertEquals("", book.name());
                assertEquals(10, book.pageNumber());
            }
        };

        bookState.attachObserver(exampleObserver);
        bookState.setState(new PageNumberChanged(10));

    }
}
