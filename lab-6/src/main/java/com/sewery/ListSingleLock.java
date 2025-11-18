package com.sewery;

import java.util.concurrent.locks.ReentrantLock;

public class ListSingleLock {
    private Node head;
    private final ReentrantLock listLock = new ReentrantLock();

    public ListSingleLock(Node head) {
        this.head = head;
    }

    boolean contains(Object value) {
        Node element = head;
        listLock.lock();
        try {
            while (element != null) {
                if (element.getValue().equals(value)) {
                    return true;
                }
                element = element.getNext();
            }
            return false;
        }finally {
            listLock.unlock();
        }
    }

    boolean remove(Node value) {
        if (value == null) {
            return true;
        }
        listLock.lock();
        try {
            if (head == null) {
                return false;
            }
            if (value == head) {
                this.head = this.head.getNext();
                return true;
            }
            Node current = head;

            while (current.getNext() != null && current.getNext() != value) {
                current = current.getNext();
            }
            if (current.getNext() == null) {
                return false;
            }
            if (current.getNext() == value) {
                current.setNext(current.getNext().getNext());
                return true;
            }
            return false;
        }finally{
            listLock.unlock();
        }

    }
    boolean add(Object value) {
        if (value == null) {
            return true;
        }

        listLock.lock();
        try {
            Node newNode = new Node(value);
            if (head == null) {
                head = newNode;
                return true;
            }
            Node current = head;
            while (current.getNext() != null) {
                current = current.getNext();
            }
            current.setNext(newNode);
            return true;
        }finally {
            listLock.unlock();
        }
    }

    public Node getHead(){
        return this.head;
    }
}
