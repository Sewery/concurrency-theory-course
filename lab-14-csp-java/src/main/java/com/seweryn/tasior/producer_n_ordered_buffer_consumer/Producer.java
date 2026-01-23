package com.seweryn.tasior.producer_n_ordered_buffer_consumer;

import org.jcsp.lang.*;

class Producer implements CSProcess {
    private final One2OneChannelInt outToBuffer0;

    public Producer(final One2OneChannelInt out) {
        this.outToBuffer0 = out;
    }

    public void run() {
        while (true) {
            // produkuj(p)
            int item = (int) (Math.random() * 100) + 1;
            // BUFFER(0) ! p
            outToBuffer0.out().write(item);
            System.out.println("Wyprodukowano do bufora 0 : " + item);
        }
    }
}