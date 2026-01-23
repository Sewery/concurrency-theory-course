package com.seweryn.tasior.producer_n_unordered_buffor_consumer_fifo;

import org.jcsp.lang.*;

class Producer implements CSProcess {
    private final One2OneChannelInt[] outToBuffers;

    public Producer(final One2OneChannelInt[] outs) {
        this.outToBuffers = outs;
    }

    public void run() {
        int inIdx = 0; // Indeks 'in' z pseudokodu
        final int N = outToBuffers.length;

        while (true) {
            // produkuj(p)
            int item = (int) (Math.random() * 100) + 1;
            
            // BUFFER(in) ! p
            outToBuffers[inIdx].out().write(item);
            
            // in := (in + 1) mod N
            inIdx = (inIdx + 1) % N;
        }
    }
}