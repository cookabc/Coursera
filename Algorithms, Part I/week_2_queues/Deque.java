import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {

    private Node head;
    private Node tail;
    private int size;

    private class Node {
        private final Item item;
        private Node pre;
        private Node next;

        public Node(Item item) {
            this.item = item;
        }
    }

    // construct an empty deque
    public Deque() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    // is the deque empty?
    public boolean isEmpty() {
        return this.size == 0;
    }

    // return the number of items on the deque
    public int size() {
        return this.size;
    }

    // add the item to the front
    public void addFirst(Item item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }
        Node oldHead = this.head;
        this.head = new Node(item);
        if (this.size == 0) {
            this.tail = this.head;
        } else {
            this.head.next = oldHead;
            oldHead.pre = this.head;
        }
        this.size++;
    }

    // add the item to the back
    public void addLast(Item item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }
        Node oldTail = this.tail;
        this.tail = new Node(item);
        if (this.size == 0) {
            this.head = this.tail;
        } else {
            this.tail.pre = oldTail;
            oldTail.next = this.tail;
        }
        this.size++;
    }

    // remove and return the item from the front
    public Item removeFirst() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        Item item = this.head.item;
        this.head = this.head.next;
        if (this.head != null) {
            this.head.pre = null;
        } else {
            this.tail = null;
        }
        this.size--;
        return item;
    }

    // remove and return the item from the back
    public Item removeLast() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        Item item = this.tail.item;
        this.tail = this.tail.pre;
        if (this.tail != null) {
            this.tail.next = null;
        } else {
            this.head = null;
        }
        this.size--;
        return item;
    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        return new DequeIterator();
    }

    private class DequeIterator implements Iterator<Item> {
        private Node current = head;

        @Override
        public boolean hasNext() {
            return this.current != null;
        }

        @Override
        public Item next() {
            if (this.current == null) {
                throw new java.util.NoSuchElementException();
            }
            Item item = this.current.item;
            this.current = this.current.next;
            return item;
        }

        @Override
        public void remove() {
            throw new java.lang.UnsupportedOperationException();
        }
    }

    // unit testing (required)
    public static void main(String[] args) {
        Deque<String> deque = new Deque<>();
        StdOut.println(deque.size());
        StdOut.println(deque.isEmpty());
        StdOut.println("=========");
        try {
            deque.removeFirst();
        } catch (NoSuchElementException e) {
            StdOut.println(e);
        }
        try {
            deque.removeLast();
        } catch (NoSuchElementException e) {
            StdOut.println(e);
        }
        StdOut.println("=========");
        deque.addFirst("First_1");
        deque.addLast("Last_1");
        deque.addLast("Last_2");
        deque.addFirst("First_2");
        try {
            deque.addLast(null);
        } catch (IllegalArgumentException e) {
            StdOut.println(e);
        }
        try {
            deque.addFirst(null);
        } catch (IllegalArgumentException e) {
            StdOut.println(e);
        }
        StdOut.println("=========");
        StdOut.println(deque.size());
        StdOut.println(deque.isEmpty());
        StdOut.println("=========");
        Iterator<String> iterator = deque.iterator();
        while (iterator.hasNext()) {
            StdOut.println(iterator.next());
        }
        try {
            iterator.remove();
        } catch (UnsupportedOperationException e) {
            StdOut.println(e);
        }
        StdOut.println("=========");
        StdOut.println(deque.removeFirst());
        StdOut.println(deque.removeLast());
        StdOut.println(deque.removeLast());
        StdOut.println("=========");
        StdOut.println(deque.size());
        StdOut.println(deque.isEmpty());
    }

}
