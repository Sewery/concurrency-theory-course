package com.seweryn.tasior.producer_n_unordered_buffor_consumer;

import org.jcsp.lang.*;

class Producer implements CSProcess {
    private final One2OneChannelInt[] dataChannels;   // Wyjście danych do buforów
    private final One2OneChannelInt[] signalChannels; // Wejście sygnałów od buforów

    public Producer(final One2OneChannelInt[] dataChannels, final One2OneChannelInt[] signalChannels) {
        this.dataChannels = dataChannels;
        this.signalChannels = signalChannels;
    }

    public void run() {
        final Guard[] guards = new Guard[signalChannels.length];
        for (int i = 0; i < signalChannels.length; i++) {
            guards[i] = signalChannels[i].in();
        }
        final Alternative alt = new Alternative(guards);

        while (true) {
            // Produkuj (p)
            int item = (int) (Math.random() * 100) + 1;

            // Czekaj na sygnał od dowolnego wolnego bufora
            int bufferIndex = alt.fairSelect();
            signalChannels[bufferIndex].in().read(); // Odbierz sygnał JESZCZE()

            // Wyślij dane do wybranego bufora
            dataChannels[bufferIndex].out().write(item);
        }
    }
}