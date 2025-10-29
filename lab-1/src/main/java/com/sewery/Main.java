package com.sewery;

import java.util.ArrayList;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws InterruptedException {
        Counter counter = new Counter();
        for(;;){
            new Thread(()->{
                int i = 1;
                int j = 1;
                for(int i1 = 0; i1 < 1000000000;i1++) {
                    i += 2;
                    j += 2;
                    i = i + j;
                    j = i / 2;
                    i1--;
                }
                int h =i+j;
                System.out.println("Sie kiedys skocznylo +"+h);

            }).start();
            counter.increment();
            System.out.println("Dodano watek "+counter.getCounter());
        }

    }
}