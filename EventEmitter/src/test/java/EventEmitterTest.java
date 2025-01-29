import org.EventEmitter.EventEmitter;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

public class EventEmitterTest {
    @Test
    void initializeEventEmitter(){
        var eventEmitter = EventEmitter.getInstance();
        assertNotNull(eventEmitter);
    }

    @Test
    void addCallbackToHashMapAndCheckSize() {
        record PrintCommand(String value) {}
        record TransactionCompletedCommand(Boolean bool){}
        var eventEmitter = EventEmitter.getInstance();

        eventEmitter.saveEvent("Print", (PrintCommand command) -> System.out.println(command.value()));
        eventEmitter.saveEvent("Transaction-Completed", (TransactionCompletedCommand command) -> System.out.println("Transaction successfully completed !! : "+command.bool()));
        assertEquals(2, eventEmitter.eventSize());
    }

    @Test
    void addCallbackToHashMapWithSameEventKeyAndCheckSize() {
        record PrintCommand(String value) {}
        var eventEmitter = EventEmitter.getInstance();
        eventEmitter.saveEvent("Print", (PrintCommand command) -> System.out.println(command.value()));
        eventEmitter.saveEvent("Print", (PrintCommand command) -> System.out.println(command.value()));
        assertEquals(1, eventEmitter.eventSize());
    }
    @Test
    void emitEventTest() throws ExecutionException, InterruptedException, TimeoutException {
        record PrintCommand(String value) {}
        var eventEmitter = EventEmitter.getInstance();
        CompletableFuture<Void> future = new CompletableFuture<>();

        eventEmitter.saveEvent("Print", (PrintCommand command) -> {
            try {
                assertEquals("Hello World", command.value());
                future.complete(null); // Başarılı olursa future tamamlandı
            } catch (AssertionError e) {
                future.completeExceptionally(e); // Hata olursa exception fırlat
            }
        });

        eventEmitter.emit("Print", new PrintCommand("Hello World"));

        future.get(1, TimeUnit.SECONDS); // 1 saniye içinde tamamlanmazsa test hata versin
    }

}
