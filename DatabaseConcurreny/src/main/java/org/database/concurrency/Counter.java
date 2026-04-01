package org.database.concurrency;

public class Counter {
    private int value = 0;
    private Counter(int i) {
        value = i;
    }
    public static Counter valueOf(int val){
        return new Counter(val);
    }
    public void increment(){value++;};
    public int value(){return value;}
}
