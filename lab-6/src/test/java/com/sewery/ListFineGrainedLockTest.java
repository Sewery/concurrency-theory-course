package com.sewery;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ListFineGrainedLockTest {

    private ListFineGrainedLock list;
    private Node nodeA;
    private Node nodeB;
    private Node nodeC;

    @BeforeEach
    void setUp() {
        nodeA = new Node("A");
        nodeB = new Node("B");
        nodeC = new Node("C");

        nodeA.setNext(nodeB);
        nodeB.setNext(nodeC);

        list = new ListFineGrainedLock(nodeA);
    }

    @Test
    void testContainsFindsExisting() {
        assertTrue(list.contains("A"), "Powinien znaleźć A");
        assertTrue(list.contains("B"), "Powinien znaleźć B");
        assertTrue(list.contains("C"), "Powinien znaleźć C");
    }

    @Test
    void testContainsHandlesMissing() {
        assertFalse(list.contains("D"), "Nie powinien znaleźć nieistniejącej wartości");
        assertFalse(list.contains(null), "Nie powinien znaleźć null");
    }

    @Test
    void testContainsOnEmptyList() {
        ListFineGrainedLock emptyList = new ListFineGrainedLock();
        assertFalse(emptyList.contains("A"), "Nie powinien nic znaleźć w pustej liście");
    }

    @Test
    void testAddAppendsToEnd() {
        assertTrue(list.add("D"));
        assertTrue(list.contains("D"), "Lista powinna zawierać nowy węzeł D");
        assertEquals("D", nodeC.getNext().getValue(), "Węzeł C powinien teraz wskazywać na D");
    }

    @Test
    void testAddOnEmptyList() {
        ListFineGrainedLock emptyList = new ListFineGrainedLock();
        assertTrue(emptyList.add("A"));
        assertTrue(emptyList.contains("A"), "Pusta lista powinna teraz zawierać A");
        assertNotNull(emptyList.getHead(), "Head nie powinien być null");
        assertEquals("A", emptyList.getHead().getValue(), "Wartość head to A");
    }

    @Test
    void testRemoveHead() {
        assertTrue(list.remove(nodeA), "Powinien zwrócić true przy pomyślnym usunięciu");
        assertEquals(nodeB, list.getHead(), "Head powinien być teraz węzeł B");
        assertFalse(list.contains("A"), "Lista nie powinna już zawierać A");
    }

    @Test
    void testRemoveMiddle() {
        assertTrue(list.remove(nodeB), "Powinien zwrócić true przy pomyślnym usunięciu");
        assertEquals(nodeC, nodeA.getNext(), "Węzeł A powinien teraz wskazywać na węzeł C");
        assertFalse(list.contains("B"), "Lista nie powinna już zawierać B");
        assertTrue(list.contains("A"));
        assertTrue(list.contains("C"));
    }

    @Test
    void testRemoveTail() {
        assertTrue(list.remove(nodeC), "Powinien zwrócić true przy pomyślnym usunięciu");
        assertNull(nodeB.getNext(), "Węzeł B powinien teraz wskazywać na null");
        assertFalse(list.contains("C"), "Lista nie powinna już zawierać C");
        assertTrue(list.contains("A"));
        assertTrue(list.contains("B"));
    }

    @Test
    void testRemoveNonExistentNode() {
        Node nodeD = new Node("D");
        assertFalse(list.remove(nodeD), "Powinien zwrócić false dla węzła, którego nie ma na liście");
        assertTrue(list.contains("A"));
        assertTrue(list.contains("B"));
        assertTrue(list.contains("C"));
    }
}
