package com.sewery;

import com.sewery.naive.NaivePhilosopher;
import com.sewery.thread_starvation.StarvationPhilosopher;
import com.sewery.asymetric.AsymmetricPhilosopher;
import com.sewery.arbiter.Arbiter;
import com.sewery.arbiter.ArbiterPhilosopher;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Main {
    private static final int NUMBER_OF_PHILOSOPHERS = 5;
    private static final int SIMULATION_TIME = 10000;
    private static final int NUMBER_OF_MEASUREMENTS = 5;

    public static void main(String[] args) {
        System.out.println("Wybierz implementacje:");
        System.out.println("0. Rozwiazanie naiwne");
        System.out.println("1. Rozwiazanie z mozliwoscia zaglodzenia");
        System.out.println("2. Rozwiazanie asymetryczne");
        System.out.println("3. Rozwiazanie z arbitrem");
        System.out.println("4. Uruchom wszystkie testy");

        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();

        try (PrintWriter writer = new PrintWriter(new FileWriter( "wyniki.csv"))) {
            writer.println("Implementacja,Czas,Filozof,CzasOczekiwania,PosilkiZjedzone");

            if (choice == 4) {
                System.out.println("\n=== Wykonywanie pomiarow dla wszystkich implementacji ===\n");
                for (int i = 0; i < 4; i++) {
                    runMeasurements(i, writer);
                }
            } else {
                runMeasurements(choice, writer);
            }
        } catch (IOException e) {
            System.err.println("Blad podczas zapisu do pliku CSV: " + e.getMessage());
        }
    }

    private static void runMeasurements(int implementationChoice, PrintWriter writer) {
        String implementationName = switch (implementationChoice) {
            case 0 -> "Naiwne";
            case 1 -> "Z zaglodzeniem";
            case 2 -> "Asymetryczne";
            case 3 -> "Z arbitrem";
            default -> "Nieznana implementacja";
        };

        System.out.println("\n=== " + implementationName + " ===");
        System.out.println("Wykonywanie " + NUMBER_OF_MEASUREMENTS + " pomiarow...\n");

        for (int i = 0; i < NUMBER_OF_MEASUREMENTS; i++) {
            System.out.println("Pomiar " + (i + 1) + ":");
            switch (implementationChoice) {
                case 0 -> saveResults(runPhilosopherSolution(NaivePhilosopher.class), implementationName, i + 1, writer);
                case 1 -> saveResults(runPhilosopherSolution(StarvationPhilosopher.class), implementationName, i + 1, writer);
                case 2 -> saveResults(runPhilosopherSolution(AsymmetricPhilosopher.class), implementationName, i + 1, writer);
                case 3 -> saveResults(runArbiterSolution(), implementationName, i + 1, writer);
            }
            System.out.println();
        }
    }

    private static void saveResults(MeasurementResult result, String implementation, int measurementNumber, PrintWriter writer) {
        for (int i = 0; i < NUMBER_OF_PHILOSOPHERS; i++) {
            writer.printf("%s,%d,%d,%.2f,%d%n",
                implementation,
                measurementNumber,
                i,
                result.waitingTimes[i],
                result.mealsEaten[i]
            );
        }
        writer.flush();
    }

    private static class MeasurementResult {
        final double[] waitingTimes;
        final int[] mealsEaten;

        MeasurementResult(double[] waitingTimes, int[] mealsEaten) {
            this.waitingTimes = waitingTimes;
            this.mealsEaten = mealsEaten;
        }
    }

    private static <T extends Philosopher> MeasurementResult runPhilosopherSolution(Class<T> philosopherClass) {
        Fork[] forks = new Fork[NUMBER_OF_PHILOSOPHERS];
        Philosopher[] philosophers = new Philosopher[NUMBER_OF_PHILOSOPHERS];

        // Initialize forks
        for (int i = 0; i < NUMBER_OF_PHILOSOPHERS; i++) {
            forks[i] = new Fork(i);
        }

        try {
            // Initialize philosophers
            for (int i = 0; i < NUMBER_OF_PHILOSOPHERS; i++) {
                Fork leftFork = forks[i];
                Fork rightFork = forks[(i + 1) % NUMBER_OF_PHILOSOPHERS];
                philosophers[i] = philosopherClass.getDeclaredConstructor(int.class, Fork.class, Fork.class)
                        .newInstance(i, leftFork, rightFork);
            }

            // Start philosophers
            for (Philosopher philosopher : philosophers) {
                philosopher.start();
            }

            // Let them run for SIMULATION_TIME
            Thread.sleep(SIMULATION_TIME);

            // Stop philosophers and collect results
            double[] waitingTimes = new double[NUMBER_OF_PHILOSOPHERS];
            int[] mealsEaten = new int[NUMBER_OF_PHILOSOPHERS];

            for (int i = 0; i < NUMBER_OF_PHILOSOPHERS; i++) {
                philosophers[i].interrupt();
                philosophers[i].join();
                waitingTimes[i] = philosophers[i].getAverageWaitingTime();
                mealsEaten[i] = philosophers[i].getMealsEaten();
                System.out.printf("Filozof %d - posilki: %d, sredni czas oczekiwania: %.2f ms%n",
                    i, mealsEaten[i], waitingTimes[i]);
            }

            return new MeasurementResult(waitingTimes, mealsEaten);

        } catch (Exception e) {
            e.printStackTrace();
            return new MeasurementResult(new double[NUMBER_OF_PHILOSOPHERS], new int[NUMBER_OF_PHILOSOPHERS]);
        }
    }

    private static MeasurementResult runArbiterSolution() {
        Philosopher[] philosophers = new Philosopher[NUMBER_OF_PHILOSOPHERS];
        Arbiter arbiter = new Arbiter(NUMBER_OF_PHILOSOPHERS);

        // Initialize forks
        try {
            // Initialize philosophers
            for (int i = 0; i < NUMBER_OF_PHILOSOPHERS; i++) {
                philosophers[i] = new ArbiterPhilosopher(i, arbiter);
            }

            // Start philosophers
            for (Philosopher philosopher : philosophers) {
                philosopher.start();
            }

            // Let them run for SIMULATION_TIME
            Thread.sleep(SIMULATION_TIME);

            // Stop philosophers and collect results
            double[] waitingTimes = new double[NUMBER_OF_PHILOSOPHERS];
            int[] mealsEaten = new int[NUMBER_OF_PHILOSOPHERS];

            for (int i = 0; i < NUMBER_OF_PHILOSOPHERS; i++) {
                philosophers[i].interrupt();
                philosophers[i].join();
                waitingTimes[i] = philosophers[i].getAverageWaitingTime();
                mealsEaten[i] = philosophers[i].getMealsEaten();
                System.out.printf("Filozof %d - posilki: %d, sredni czas oczekiwania: %.2f ms%n",
                    i, mealsEaten[i], waitingTimes[i]);
            }

            return new MeasurementResult(waitingTimes, mealsEaten);

        } catch (Exception e) {
            e.printStackTrace();
            return new MeasurementResult(new double[NUMBER_OF_PHILOSOPHERS], new int[NUMBER_OF_PHILOSOPHERS]);
        }
    }
}
