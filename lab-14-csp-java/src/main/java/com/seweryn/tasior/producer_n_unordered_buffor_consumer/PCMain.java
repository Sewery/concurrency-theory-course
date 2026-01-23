package com.seweryn.tasior.producer_n_unordered_buffor_consumer;

import org.jcsp.lang.*;

public final class PCMain {
    public static void main(String[] args) {
        final int N = 5; // Rozmiar bufora (liczba procesów bufora)

        // Kanały między Producentem a Buforami
        final One2OneChannelInt[] prodToBufferData = Channel.one2oneIntArray(N);
        final One2OneChannelInt[] bufferToProdSignal = Channel.one2oneIntArray(N);

        // Kanały między Buforami a Konsumentem
        final One2OneChannelInt[] bufferToConsData = Channel.one2oneIntArray(N);

        CSProcess[] procList = new CSProcess[N + 2];

        // Tworzenie N procesów bufora
        for (int i = 0; i < N; i++) {
            procList[i] = new Buffer(prodToBufferData[i], bufferToProdSignal[i], bufferToConsData[i]);
        }

        // Dodanie producenta i konsumenta
        procList[N] = new Producer(prodToBufferData, bufferToProdSignal);
        procList[N + 1] = new Consumer(bufferToConsData);

        Parallel par = new Parallel(procList);
        par.run();
    }
}