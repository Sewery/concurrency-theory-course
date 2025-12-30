package com.sewery.asymetric;

import com.sewery.Fork;
import com.sewery.Philosopher;

public class AsymmetricPhilosopher extends Philosopher {

    public AsymmetricPhilosopher(int id, Fork leftFork, Fork rightFork) {
        super(id, id % 2 == 0 ? leftFork : rightFork, id % 2 == 0 ? rightFork : leftFork);
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                think();

                startWaiting();
                System.out.println("Filozof " + id + " probuje podniesc pierwszy widelec " + firstFork.getId());
                firstFork.take();
                System.out.println("Filozof " + id + " podniosl pierwszy widelec " + firstFork.getId());

                System.out.println("Filozof " + id + " probuje podniesc drugi widelec " + secondFork.getId());
                secondFork.take();
                stopWaiting();
                System.out.println("Filozof " + id + " podniosl drugi widelec " + secondFork.getId());

                eat();

                secondFork.putDown();
                System.out.println("Filozof " + id + " odlozyl drugi widelec " + secondFork.getId());
                firstFork.putDown();
                System.out.println("Filozof " + id + " odlozyl pierwszy widelec " + firstFork.getId());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
