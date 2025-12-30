package com.sewery.arbiter;

import com.sewery.Philosopher;

public class ArbiterPhilosopher extends Philosopher {
    private final Arbiter arbiter;

    public ArbiterPhilosopher(int id, Arbiter arbiter) {
        super(id, null, null);
        this.arbiter = arbiter;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                think();

                System.out.println("Filozof " + id + " probuje podniesc widelce");
                startWaiting();
                arbiter.takeForks(id);
                stopWaiting();
                System.out.println("Filozof " + id + " podniosl widelce");

                eat();

                arbiter.putDownForks(id);
                System.out.println("Filozof " + id + " odlozyl widelce");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
