package com.sewery;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ListSingleLockTest {

    private ListSingleLock list;
    private Node nodeA;
    private Node nodeB;
    private Node nodeC;

    @BeforeEach
    void setUp() {
        // We set up a list: A -> B -> C
        // Note: The Node class constructor requires a value.
        nodeA = new Node("A");
        nodeB = new Node("B");
        nodeC = new Node("C");

        nodeA.setNext(nodeB);
        nodeB.setNext(nodeC);

        // Initialize the list with nodeA as the head
        list = new ListSingleLock(nodeA);
    }

    @Test
    void testContainsFindsExisting() {
        assertTrue(list.contains("A"), "Should find head");
        assertTrue(list.contains("B"), "Should find middle");
        assertTrue(list.contains("C"), "Should find tail");
    }

    @Test
    void testContainsHandlesMissing() {
        assertFalse(list.contains("D"), "Should not find non-existent value");
        assertFalse(list.contains(null), "Should not find null");
    }

    @Test
    void testContainsOnEmptyList() {
        ListSingleLock emptyList = new ListSingleLock(null);
        assertFalse(emptyList.contains("A"), "Should not find in empty list");
    }

    @Test
    void testAddAppendsToEnd() {
        assertTrue(list.add("D"));
        assertTrue(list.contains("D"), "List should contain new node D");
        assertEquals(nodeC.getNext().getValue(), "D", "Node C should point to new node D");
    }

    @Test
    void testAddOnEmptyList() {
        ListSingleLock emptyList = new ListSingleLock(null);
        assertTrue(emptyList.add("A"));
        assertTrue(emptyList.contains("A"), "Empty list should now contain A");
        assertNotNull(emptyList.getHead(), "Head should not be null");
        assertEquals("A", emptyList.getHead().getValue(), "Head should have value A");
    }

    @Test
    void testRemoveHead() {
        assertTrue(list.remove(nodeA), "Should return true on successful removal");
        assertEquals(nodeB, list.getHead(), "Head should now be node B");
        assertFalse(list.contains("A"), "List should no longer contain A");
    }

    @Test
    void testRemoveMiddle() {
        assertTrue(list.remove(nodeB), "Should return true on successful removal");
        assertEquals(nodeC, nodeA.getNext(), "Node A should now point to node C");
        assertFalse(list.contains("B"), "List should no longer contain B");
        assertTrue(list.contains("A"));
        assertTrue(list.contains("C"));
    }

    @Test
    void testRemoveTail() {
        assertTrue(list.remove(nodeC), "Should return true on successful removal");
        assertNull(nodeB.getNext(), "Node B should now point to null");
        assertFalse(list.contains("C"), "List should no longer contain C");
        assertTrue(list.contains("A"));
        assertTrue(list.contains("B"));
    }

    @Test
    void testRemoveNonExistentNode() {
        Node nodeD = new Node("D");
        assertFalse(list.remove(nodeD), "Should return false for a node not in the list");
        assertTrue(list.contains("A"));
        assertTrue(list.contains("B"));
        assertTrue(list.contains("C"));
    }
}