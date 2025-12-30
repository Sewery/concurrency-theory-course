package com.sewery.cw2;

import com.sewery.cw1.Buffer;

import java.util.function.Function;

class Processor extends Thread {
    private final Buffer inputBuffer;
    private final Buffer outputBuffer;
    private final Function<Integer,Integer> process;

    public Processor(Buffer input, Buffer output, String name, Function<Integer,Integer> process) {
        super(name);
        this.inputBuffer = input;
        this.outputBuffer = output;
        this.process = process;
    }

    @Override
    public void run() {
        for (int i = 0; i < 100; ++i) {
            int value = inputBuffer.get(); // Pobiera dane od poprzednika
            System.out.println("Procesor " + getName() + " pobrał: " + value);

            // Symulacja przetwarzania danych (np. dodanie wartości)
            value = process.apply(value);
            System.out.println("Procesor " + getName() + " przetworzył na: " + value);

            try {
                // Symulacja różnej prędkości działania
                Thread.sleep((long) (Math.random() * 100));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            outputBuffer.put(value); // Wstawia wynik dla następnika
        }
    }
}