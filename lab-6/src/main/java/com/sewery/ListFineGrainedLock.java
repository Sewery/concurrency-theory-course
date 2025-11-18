package com.sewery;

public class ListFineGrainedLock {
    private Node head;
    public ListFineGrainedLock(Node head){
        this.head = head;
    }
    public ListFineGrainedLock(){
    }

    boolean contains(Object value) {
        Node current = head;
        try {
            if (current == null) {
                return false;
            }
            current.lock();
            if(current.getValue().equals(value)){
                return true;
            }
            if (current.getNext() == null) {
                return false;
            }
            current.getNext().lock();
            Node next = current.getNext();
            current.unlock();
            current = next;
            while (!current.getValue().equals(value)) {
                if (current.getNext() == null) {
                    return false;
                }
                current.getNext().lock();
                next = current.getNext();
                current.unlock();
                current = next;
            }
            if (current.getValue().equals(value)) {
                return true;
            }
            return false;
        }finally{
            if(current != null) current.unlock();
        }
    }

    boolean remove(Node value) {
        if (value == null) {
            return true;
        }
        Node current = head;
        try {
            if (current == null) {
                return false;
            }
            current.lock();
            if (value == current) {
                this.head = this.head.getNext();
                return true;
            }
            while (current != null) {
                if (current.getNext() == null) {
                    return false;
                }
                current.getNext().lock();
                Node next = current.getNext();
                if (next == value) {
                    if(next.getNext()==null){
                        current.setNext(null);
                        next.unlock();
                        return true;
                    }
                    next.getNext().lock();
                    Node nextNext = next.getNext();
                    current.setNext(nextNext);
                    next.setNext(null);
                    next.unlock();
                    nextNext.unlock();
                    return true;
                }
                current = next;
            }

            return false;
        } finally {
            if (current != null) current.unlock();
        }
    }

    boolean add(Object value) {
        if (value == null) {
            return false;
        }
        Node current = head;
        try {
            if (current == null) {
                head = new Node(value);
                return true;
            }
            current.lock();
            if (current.getNext() == null) {
                current.setNext(new Node(value));
                return true;
            }
            while (current.getNext()!=null) {
                current.getNext().lock();
                Node next = current.getNext();
                current.unlock();
                current = next;

            }
            current.setNext(new Node(value));
            return true;
        }finally{
            if(current != null) current.unlock();
        }
    }

    public Node getHead(){
        return this.head;
    }
}
