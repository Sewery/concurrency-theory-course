package com.sewery.cw1;

public class Producer extends Thread {
    private Buffer buf;

    public Producer(Buffer buf, String name) {
        super(name);
        this.buf = buf;
    }

    public void run() {
        for (int i = 0; i < 100; ++i) {
            buf.put(i);
            try {
                Thread.sleep((long) (Math.random() * 100));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
