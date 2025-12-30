package com.sewery.naive;

import com.sewery.Fork;
import com.sewery.Philosopher;

public class NaivePhilosopher extends Philosopher {

    public NaivePhilosopher(int id, Fork leftFork, Fork rightFork) {
        super(id, leftFork, rightFork);
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                think();

                startWaiting();
                System.out.println("Filozof " + id + " probuje podniesc lewy widelec " + firstFork.getId());
                firstFork.take();
                System.out.println("Filozof " + id + " podniosl lewy widelec " + firstFork.getId());

                System.out.println("Filozof " + id + " probuje podniesc prawy widelec " + secondFork.getId());
                secondFork.take();
                stopWaiting();
                System.out.println("Filozof " + id + " podniosl prawy widelec " + secondFork.getId());

                eat();

                secondFork.putDown();
                System.out.println("Filozof " + id + " odlozyl prawy widelec " + secondFork.getId());
                firstFork.putDown();
                System.out.println("Filozof " + id + " odlozyl lewy widelec " + firstFork.getId());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
