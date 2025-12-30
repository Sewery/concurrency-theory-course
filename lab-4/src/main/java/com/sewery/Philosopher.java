package com.sewery;

public abstract class Philosopher extends Thread {
    protected int id;
    protected Fork firstFork;
    protected Fork secondFork;
    protected int mealsEaten;
    protected long totalWaitingTime;
    protected long startWaitingTime;

    protected Philosopher(int id, Fork firstFork, Fork secondFork) {
        this.id = id;
        this.firstFork = firstFork;
        this.secondFork = secondFork;
        this.mealsEaten = 0;
        this.totalWaitingTime = 0;
    }

    protected void think() throws InterruptedException {
        System.out.println("Filozof " + id + " mysli");
        Thread.sleep((long) (Math.random() * 100));
    }

    protected void eat() throws InterruptedException {
        System.out.println("Filozof " + id + " je");
        mealsEaten++;
        Thread.sleep((long) (Math.random() * 100));
    }

    protected void startWaiting() {
        startWaitingTime = System.currentTimeMillis();
    }

    protected void stopWaiting() {
        totalWaitingTime += System.currentTimeMillis() - startWaitingTime;
    }

    public int getMealsEaten() {
        return mealsEaten;
    }

    public double getAverageWaitingTime() {
        return mealsEaten > 0 ? (double) totalWaitingTime / mealsEaten : 0;
    }
}
