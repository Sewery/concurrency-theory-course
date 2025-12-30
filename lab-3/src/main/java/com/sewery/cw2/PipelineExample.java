package com.sewery.cw2;

import com.sewery.cw1.Buffer;
import com.sewery.cw1.Consumer;
import com.sewery.cw1.Producer;

public class PipelineExample {
    public static void main(String[] args) {
        // Definiujemy liczbę etapów i rozmiar buforów
        final int BUFFER_SIZE = 5;

        // Tworzymy bufory, które połączą etapy
        Buffer bufferA_B = new Buffer(BUFFER_SIZE);
        Buffer bufferB_C = new Buffer(BUFFER_SIZE);
        Buffer bufferC_Z = new Buffer(BUFFER_SIZE);

        // Tworzymy procesy (wątki)
        // Proces A: Producent
        Producer producerA = new Producer(bufferA_B, "A");

        // Procesy B i C: Przetwarzające
        Processor processorB = new Processor(bufferA_B, bufferB_C, "B",(val)->val*2);
        Processor processorC = new Processor(bufferB_C, bufferC_Z, "C",(val)->val+2);

        // Proces Z: Konsument
        Consumer consumerZ = new Consumer(bufferC_Z, "Z");

        System.out.println("Uruchamianie potoku: A -> B -> C -> Z");

        // Uruchamiamy wszystkie wątki
        producerA.start();
        processorB.start();
        processorC.start();
        consumerZ.start();
    }
}
