package com.seweryn.tasior.producer_n_unordered_buffor_consumer;

import org.jcsp.lang.*;

class Consumer implements CSProcess {
    private final One2OneChannelInt[] inChannels;

    public Consumer(final One2OneChannelInt[] inChannels) {
        this.inChannels = inChannels;
    }

    public void run() {
        final Guard[] guards = new Guard[inChannels.length];
        for (int i = 0; i < inChannels.length; i++) {
            guards[i] = inChannels[i].in();
        }
        final Alternative alt = new Alternative(guards);

        while (true) {
            int index = alt.fairSelect();
            int item = inChannels[index].in().read();
            System.out.println("Skonsumowano: " + item + " z bufora " + index);
        }
    }
}