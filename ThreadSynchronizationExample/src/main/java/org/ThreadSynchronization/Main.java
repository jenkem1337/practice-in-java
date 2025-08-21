package org.ThreadSynchronization;

public class Main {
    public static void main(String[] args) {
        var c3 = new ThirdComponent();
        var c2 = new SecondComponent(c3);
        var c1 = new FirstComponent(c2);

        Thread t1 = new Thread(c1::execute);
        Thread t2 = new Thread(c2::execute);
        Thread t3 = new Thread(c3::execute);

        t1.start();
        t2.start();
        t3.start();
    }
}