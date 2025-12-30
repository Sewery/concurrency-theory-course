package com.sewery.arbiter;

public class Arbiter {
    private final int numberOfPhilosophers;
    private final boolean[] forks;
    private int eatingCount;

    public Arbiter(int numberOfPhilosophers) {
        this.numberOfPhilosophers = numberOfPhilosophers;
        this.forks = new boolean[numberOfPhilosophers];
        this.eatingCount = 0;
    }

    public synchronized boolean takeForks(int philosopherId) throws InterruptedException {
        // Sprawdzamy, czy filozof może jeść (maksymalnie n-1 filozofów może jeść jednocześnie)
        while (eatingCount >= numberOfPhilosophers - 1 ||
               forks[philosopherId] ||
               forks[(philosopherId + 1) % numberOfPhilosophers]) {
            wait();
        }

        // Przydzielamy widelce
        forks[philosopherId] = true;
        forks[(philosopherId + 1) % numberOfPhilosophers] = true;
        eatingCount++;
        return true;
    }

    public synchronized void putDownForks(int philosopherId) {
        forks[philosopherId] = false;
        forks[(philosopherId + 1) % numberOfPhilosophers] = false;
        eatingCount--;
        notifyAll();
    }
}
