package com.sewery;

import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class Node {
    private Object value;
    private Node next;
    private final ReentrantLock lock;

    public Node(Object value) {
        this.value = Objects.requireNonNull(value);
        this.lock = new ReentrantLock();
    }

    public Object getValue() {
        return value;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }

}
