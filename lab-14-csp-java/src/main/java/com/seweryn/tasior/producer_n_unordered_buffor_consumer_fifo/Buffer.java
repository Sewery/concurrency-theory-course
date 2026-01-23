package com.seweryn.tasior.producer_n_unordered_buffor_consumer_fifo;

import org.jcsp.lang.*;

public class Buffer implements CSProcess {
    private final One2OneChannelInt inFromProducer;
    private final One2OneChannelInt outToConsumer;

    public Buffer(final One2OneChannelInt in, final One2OneChannelInt out) {
        this.inFromProducer = in;
        this.outToConsumer = out;
    }

    public void run() {
        while (true) {
            // PRODUCER ? p
            int item = inFromProducer.in().read();
            // CONSUMER ! p
            outToConsumer.out().write(item);
        }
    }
}