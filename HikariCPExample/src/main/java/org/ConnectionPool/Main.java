package org.ConnectionPool;

public class Main {
    public static void main(String[] args) {
        var dao = UserDao.hikariCp();

        for(int i = 0; i < 10_000; i++) {

            new Thread(() -> {

                for(User u : dao.findAllUsers()) {
                    System.out.println(u);
                }

            }).start();
        }
    }
}