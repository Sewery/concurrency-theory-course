package com.seweryn.tasior.producer_consumer;

import org.jcsp.lang.CSProcess;
import org.jcsp.lang.One2OneChannelInt;

class Consumer implements CSProcess {
    private One2OneChannelInt channel;
    public Consumer(final One2OneChannelInt in ) {
        channel = in;
    }
    public void run() {
        int item = channel.in().read();
        System.out.println(item);
    }
}