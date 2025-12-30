package com.sewery;

public class Fork {
    private final int id;
    private boolean taken = false;

    public Fork(int id) {
        this.id = id;
    }

    public synchronized void take() throws InterruptedException {
        while (taken) {
            wait();
        }
        taken = true;
    }

    public synchronized void putDown() {
        taken = false;
        notify();
    }

    public int getId() {
        return id;
    }

    public synchronized boolean isTaken() {
        return taken;
    }
}
