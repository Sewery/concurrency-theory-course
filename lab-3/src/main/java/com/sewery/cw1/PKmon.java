package com.sewery.cw1;

public class PKmon {
    public static void main(String[] args) {
        //case1Consumer1Producer();
        caseNConsumerNProducer(3,3);

    }
    public static void case1Consumer1Producer() {
        int bufferSize = 10;
        Buffer buffer = new Buffer(bufferSize);
        new Producer(buffer, "Producent").start();
        new Consumer(buffer, "Konsument").start();
    }
    public static void caseNConsumerNProducer(int numProducers, int numConsumers) {

        int bufferSize = 10;
        Buffer buffer = new Buffer(bufferSize);
        // Tworzenie i uruchamianie wątków producentów
        for (int i = 0; i < numProducers; i++) {
            new Producer(buffer, "Producent-" + (i + 1)).start();
        }

        // Tworzenie i uruchamianie wątków konsumentów
        for (int i = 0; i < numConsumers; i++) {
            new Consumer(buffer, "Konsument-" + (i + 1)).start();
        }

        System.out.println("Uruchomiono " + numProducers + " producentow i " + numConsumers + " konsumentow.");
    }
}
