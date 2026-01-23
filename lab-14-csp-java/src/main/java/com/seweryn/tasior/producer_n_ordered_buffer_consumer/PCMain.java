package com.seweryn.tasior.producer_n_ordered_buffer_consumer;

import org.jcsp.lang.*;

public final class PCMain {
    public static void main(String[] args) {
        final int N = 5; // Rozmiar bufora (liczba procesów)

        final One2OneChannelInt[] channels = Channel.one2oneIntArray(N + 1);

        CSProcess[] procList = new CSProcess[N + 2];

        procList[0] = new Producer(channels[0]);

        for (int i = 0; i < N; i++) {
            procList[i + 1] = new Buffer(channels[i], channels[i + 1]);
        }

        procList[N + 1] = new Consumer(channels[N]);

        Parallel par = new Parallel(procList);
        par.run();
    }
}