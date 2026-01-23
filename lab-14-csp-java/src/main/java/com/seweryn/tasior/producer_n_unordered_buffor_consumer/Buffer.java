package com.seweryn.tasior.producer_n_unordered_buffor_consumer;

import org.jcsp.lang.*;

public class Buffer implements CSProcess {
    private final One2OneChannelInt inFromProducer;
    private final One2OneChannelInt outToProducerSignal;
    private final One2OneChannelInt outToConsumer;

    public Buffer(final One2OneChannelInt inFromProducer,
                  final One2OneChannelInt outToProducerSignal,
                  final One2OneChannelInt outToConsumer) {
        this.inFromProducer = inFromProducer;
        this.outToProducerSignal = outToProducerSignal;
        this.outToConsumer = outToConsumer;
    }

    public void run() {
        while (true) {
            //(PRODUCER ! JESZCZE)
            outToProducerSignal.out().write(0);

            //  (PRODUCER ? p)
            int item = inFromProducer.in().read();

            //  (CONSUMER ! p)
            outToConsumer.out().write(item);
        }
    }
}