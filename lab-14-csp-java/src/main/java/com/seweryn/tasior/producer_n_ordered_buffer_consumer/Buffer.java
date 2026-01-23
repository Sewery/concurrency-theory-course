package com.seweryn.tasior.producer_n_ordered_buffer_consumer;

import org.jcsp.lang.*;

public class Buffer implements CSProcess {
    private final One2OneChannelInt in;
    private final One2OneChannelInt out;

    public Buffer(final One2OneChannelInt in, final One2OneChannelInt out) {
        this.in = in;
        this.out = out;
    }

    public void run() {
        while (true) {
            // BUFFER(i) ? p
            int item = in.in().read();
            // BUFFER(i+1) ! p lub CONSUMER ! p
            out.out().write(item);
        }
    }
}