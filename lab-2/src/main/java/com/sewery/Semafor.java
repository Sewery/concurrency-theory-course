package com.sewery;

public class Semafor {
    private boolean state = true;
    private int wait = 0;

    public Semafor() {

    }

    public synchronized void P() {
        wait--;
        while (wait<1) {
            try {
                wait();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        state = true;
    }

    public synchronized void V() {
        state = false;
        wait++;
        notify();
    }
}
