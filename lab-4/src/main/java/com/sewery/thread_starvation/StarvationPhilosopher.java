package com.sewery.thread_starvation;

import com.sewery.Fork;
import com.sewery.Philosopher;

public class StarvationPhilosopher extends Philosopher {
    private static final Object globalLock = new Object();

    public StarvationPhilosopher(int id, Fork leftFork, Fork rightFork) {
        super(id, leftFork, rightFork);
    }

    @Override
    public void run(){
        Fork leftFork = firstFork;
        Fork rightFork = secondFork;
        try {
            while (!Thread.interrupted()) {
                think();

                startWaiting();
                synchronized (globalLock) {
                    System.out.println("Filozof " + id + " probuje podniesc widelce");

                    while (leftFork.isTaken() || rightFork.isTaken()) {
                        globalLock.wait();
                    }

                    leftFork.take();
                    rightFork.take();
                    stopWaiting();
                    System.out.println("Filozof " + id + " podniosl oba widelce");
                }

                eat();

                synchronized (globalLock) {
                    leftFork.putDown();
                    rightFork.putDown();
                    System.out.println("Filozof " + id + " odlozyl widelce");
                    globalLock.notifyAll();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
