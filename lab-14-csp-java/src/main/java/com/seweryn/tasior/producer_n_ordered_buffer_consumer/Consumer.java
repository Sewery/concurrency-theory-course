package com.seweryn.tasior.producer_n_ordered_buffer_consumer;

import org.jcsp.lang.*;

class Consumer implements CSProcess {
    private final One2OneChannelInt inFromBufferN;

    public Consumer(final One2OneChannelInt in) {
        this.inFromBufferN = in;
    }

    public void run() {
        while (true) {
            // BUFFER(N-1) ? p
            int item = inFromBufferN.in().read();
            // konsumuj(p)
            System.out.println("Skonsumowano  z bufora N-1 : " + item);
        }
    }
}