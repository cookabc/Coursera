import java.util.Iterator;
import java.util.Random;

import edu.princeton.cs.algs4.StdRandom;

public class RandomizedQueue<Item> implements Iterable<Item> {
    private Item[] items;
    private int size;
    private Random random;

    // construct an empty randomized queue
    public RandomizedQueue() {
        items = (Item[]) new Object[1];
        size = 0;
        random = new Random();
    }

    // is the queue empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // return the number of items on the queue
    public int size() {
        return size;
    }

    // add the item
    public void enqueue(Item item) {
        if (item == null) {
            throw new java.lang.NullPointerException();
        }
        if (items.length == size) {
            resize(items.length * 2);
        }
        items[size++] = item;
    }

    // remove and return a random item
    public Item dequeue() {
        if (isEmpty()) {
            throw new java.util.NoSuchElementException();
        }
        // int index = StdRandom.uniform(size);
        int index = random.nextInt(size);
        Item item = items[index];
        items[index] = items[--size];
        items[size] = null;
        if (size > 0 && items.length / 4 == size) {
            resize(items.length / 2);
        }
        return item;
    }

    private void resize(int newCap) {
        Item[] newItems = (Item[]) new Object[newCap];
        for (int i = 0; i < size; i++) {
            newItems[i] = items[i];
        }
        items = newItems;
    }

    // return (but do not remove) a random item
    public Item sample() {
        if (isEmpty()) {
            throw new java.util.NoSuchElementException();
        }
        // return items[StdRandom.uniform(size)];
        return items[random.nextInt(size)];
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new RqIterator();
    }

    private class RqIterator implements Iterator<Item> {
        private int current = 0;
        private Item[] iterItems;

        public RqIterator() {
            iterItems = (Item[]) new Object[size];
            System.arraycopy(items, 0, iterItems, 0, size);
            StdRandom.shuffle(iterItems);
        }

        public boolean hasNext() {
            return current < size;
        }

        public Item next() {
            if (!hasNext()) {
                throw new java.util.NoSuchElementException();
            }
            return iterItems[current++];
        }

        public void remove() {
            throw new java.lang.UnsupportedOperationException();
        }
    }
}