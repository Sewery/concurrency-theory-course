package com.sewery;

public class Counter {
    private int counter;

    public Counter() {
        counter = 0;
    }

    public int getCounter() {
        return counter;
    }

    public synchronized void incrementSync() {
        counter++;
    }
    public synchronized void decrementSync() {
        counter--;
    }
    public void increment() {
        counter++;
    }
    public void decrement() {
        counter--;
    }
}
