package com.seweryn.tasior.producer_n_unordered_buffor_consumer_fifo;

import org.jcsp.lang.*;

class Consumer implements CSProcess {
    private final One2OneChannelInt[] inFromBuffers;

    public Consumer(final One2OneChannelInt[] ins) {
        this.inFromBuffers = ins;
    }

    public void run() {
        int outIdx = 0; // Indeks 'out' z pseudokodu
        final int N = inFromBuffers.length;

        while (true) {
            // BUFFER(out) ? p
            int item = inFromBuffers[outIdx].in().read();
            
            // konsumuj(p)
            System.out.println("Skonsumowano (FIFO): " + item + " z bufora [" + outIdx + "]");
            
            // out := (out + 1) mod N
            outIdx = (outIdx + 1) % N;
        }
    }
}