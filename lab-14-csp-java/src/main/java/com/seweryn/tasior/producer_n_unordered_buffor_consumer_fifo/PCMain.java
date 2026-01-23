package com.seweryn.tasior.producer_n_unordered_buffor_consumer_fifo;

import org.jcsp.lang.*;

public final class PCMain {
    public static void main(String[] args) {
        final int N = 10; // Rozmiar bufora (liczba procesów)

        final One2OneChannelInt[] prodToBufs = Channel.one2oneIntArray(N);
        final One2OneChannelInt[] bufsToCons = Channel.one2oneIntArray(N);

        CSProcess[] procList = new CSProcess[N + 2];
        for (int i = 0; i < N; i++) {
            procList[i] = new Buffer(prodToBufs[i], bufsToCons[i]);
        }

        procList[N] = new Producer(prodToBufs);
        procList[N + 1] = new Consumer(bufsToCons);

        Parallel par = new Parallel(procList);
        par.run();
    }
}