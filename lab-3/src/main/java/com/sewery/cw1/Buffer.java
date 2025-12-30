package com.sewery.cw1;

public class Buffer {
    private final int[] buffer;
    private int count = 0;
    private int in = 0; // indeks do wstawiania
    private int out = 0; // indeks do pobierania

    public Buffer(int size) {
        this.buffer = new int[size];
    }

    public synchronized void put(int value) {
        // Oczekiwanie w pętli 'while', gdy bufor jest pełny
        while (count == buffer.length) {
            try {
                System.out.println("Bufor pelny,"+Thread.currentThread().getName() + " czeka.");
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        // Wstawienie elementu
        buffer[in] = value;
        in = (in + 1) % buffer.length;
        count++;
        System.out.println(Thread.currentThread().getName() + " dodal: " + value);
        // Obudź wszystkie oczekujące wątki (konsumentów i producentów)
        notifyAll();
    }

    public synchronized int get() {
        // Oczekiwanie w pętli 'while', gdy bufor jest pusty
        while (count == 0) {
            try {
                System.out.println("Bufor pusty, konsument " + Thread.currentThread().getName() + " czeka.");
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        // Pobranie elementu
        int value = buffer[out];
        out = (out + 1) % buffer.length;
        count--;
        // System.out.println("Konsument " + Thread.currentThread().getName() + " pobrał: " + value);
        // Obudź wszystkie oczekujące wątki (producentów i konsumentów)
        notifyAll();
        return value;
    }
}