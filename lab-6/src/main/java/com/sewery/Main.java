package com.sewery;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    static void main() {
        int listSize = 1000;
        int operations = 10000;
        int threadCount = 20;

        System.out.println("=== Performance Comparison ===");
        System.out.println("List size: " + listSize);
        System.out.println("Operations per thread: " + operations);
        System.out.println("Thread count: " + threadCount);
        System.out.println();

        // Test SingleLock
        long singleLockTime = testList(createSingleLockList(listSize), operations, threadCount, "SingleLock");

        // Test FineGrainedLock
        long fineGrainedTime = testList(createFineGrainedList(listSize), operations, threadCount, "FineGrainedLock");

        System.out.println();
        System.out.println("=== Results ===");
        System.out.println("SingleLock: " + singleLockTime + " ms");
        System.out.println("FineGrainedLock: " + fineGrainedTime + " ms");

        double speedup = (double) singleLockTime / fineGrainedTime;
        System.out.println("Speedup: " + String.format("%.2f", speedup) + "x");
    }

    private static ListSingleLock createSingleLockList(int size) {
        Node head = new Node(0);
        Node current = head;
        for (int i = 1; i < size; i++) {
            Node newNode = new Node(i);
            current.setNext(newNode);
            current = newNode;
        }
        return new ListSingleLock(head);
    }

    private static ListFineGrainedLock createFineGrainedList(int size) {
        Node head = new Node(0);
        Node current = head;
        for (int i = 1; i < size; i++) {
            Node newNode = new Node(i);
            current.setNext(newNode);
            current = newNode;
        }
        return new ListFineGrainedLock(head);
    }

    private static long testList(Object list, int operations, int threadCount, String name) {
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Runnable> tasks = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            tasks.add(() -> {
                Random random = new Random();
                for (int j = 0; j < operations; j++) {
                    int op = random.nextInt(10);
                    int value = random.nextInt(1000);

                    if (op < 7) { // 70% contains
                        if (list instanceof ListSingleLock) {
                            ((ListSingleLock) list).contains(value);
                        } else {
                            ((ListFineGrainedLock) list).contains(value);
                        }
                    } else { // 30% add
                        if (list instanceof ListSingleLock) {
                            ((ListSingleLock) list).add(value);
                        } else {
                            ((ListFineGrainedLock) list).add(value);
                        }
                    }
                }
            });
        }

        long startTime = System.currentTimeMillis();

        for (Runnable task : tasks) {
            executor.submit(task);
        }

        executor.shutdown();
        try {
            executor.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println(name + " completed in " + duration + " ms");
        return duration;
    }
}
