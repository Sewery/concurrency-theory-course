package com.sewery.cw1;

public class Consumer  extends Thread{
    private final Buffer buf;

    public Consumer(Buffer buf, String name) {
        super(name);
        this.buf = buf;
    }
    public void run() {
        for (int i = 0; i < 100; ++i) {
            int value = buf.get();
            System.out.println(Thread.currentThread().getName() + " pobral: " + value);
            try {
                // Symulacja czasu potrzebnego na skonsumowanie elementu
                Thread.sleep((long) (Math.random() * 150));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
