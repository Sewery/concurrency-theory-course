package com.sewery;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws InterruptedException {
        Counter counter = new Counter();
        Semafor semafor = new Semafor();
        semafor.V();
        var thread1 = new Thread(()->{
            for (int i = 0; i < 1e6; i++) {
                semafor.P();
                counter.increment();
                semafor.V();
            }
        });
        var thread2 = new Thread(()->{
            for (int i = 0; i < 1e6; i++) {
                semafor.P();
                counter.decrement();
                semafor.V();
            }
        });
        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        System.out.println("Counter: " + counter.getCounter());

    }
}