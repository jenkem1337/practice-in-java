package org.Algorithms;

public class CounterConsumer implements Consumer{
    private Integer counter = 0;

    public CounterConsumer(){
    }
    @Override
    public ResponseMessage onMessage(RequestMessage msg) throws InterruptedException {
        ResponseMessage response = null;
        switch ((String) msg.message()) {
            case "INCREMENT" -> {
                counter++;
            }
            case "DECREMENT" -> {
                counter--;
            }
            case "GET" -> {
                response = new ResponseMessage("Counter -> " + counter);
            }
            default -> throw new IllegalStateException("Unexpected value: " + msg);
        }
        return response;
    }

    public Integer counter() {
        return counter;
    }
}
