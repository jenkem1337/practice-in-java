package org.Journal;

import java.io.IOException;
import java.rmi.RemoteException;

public class Main {
    public static void main(String[] args) throws IOException {
        Journal logger = new BasicJournal(60);
        try{
            var firstTransaction = logger.beginTransaction();
            var secondTransaction = logger.beginTransaction();
            logger.write(firstTransaction, LogCommand.INSERT, "Hello", "World", null);
            logger.write(secondTransaction, LogCommand.INSERT, "Merhaba", "Dunya", null);
            logger.commit(secondTransaction);
            Thread.sleep(10*1000);
            logger.rollback(firstTransaction);
            throw new Exception("Something went wrong :(");
        }catch (Exception runtimeException) {
            logger = null;
            Journal newJournal = new BasicJournal(100000);
            System.out.println(runtimeException.getMessage());
            System.out.println("Recovery start !");
            newJournal.recovery();
            System.out.println("Recovery end !");

        }
    }
}