import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.Random;

public class RandomizedQueue<Item> implements Iterable<Item> {

    private Item[] itemArray;
    private int size;
    private final Random random;

    // construct an empty randomized queue
    public RandomizedQueue() {
        this.itemArray = this.createEmptyArray(1);
        this.size = 0;
        this.random = new Random();
    }

    // is the randomized queue empty?
    public boolean isEmpty() {
        return this.size <= 0;
    }

    // return the number of items on the randomized queue
    public int size() {
        return this.size;
    }

    // add the item
    public void enqueue(Item item) {
        if (item == null) {
            throw new java.lang.IllegalArgumentException();
        }
        this.resizeItemArray();
        this.itemArray[this.size] = item;
        this.size++;
    }

    // remove and return a random item
    public Item dequeue() {
        if (this.isEmpty()) {
            throw new java.util.NoSuchElementException();
        }
        int index = StdRandom.uniformInt(this.size);
        Item item = this.itemArray[index];
        this.itemArray[index] = this.itemArray[--this.size];
        this.itemArray[this.size] = null;
        this.resizeItemArray();
        return item;
    }

    // return a random item (but do not remove it)
    public Item sample() {
        if (this.isEmpty()) {
            throw new java.util.NoSuchElementException();
        }
        return this.itemArray[this.random.nextInt(this.size)];
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new Iterator<Item>() {
            private final Item[] newArray = copyArray(itemArray, size());
            private int cursor = 0;

            @Override
            public boolean hasNext() {
                return this.cursor < this.newArray.length;
            }

            @Override
            public Item next() {
                if (this.cursor >= this.newArray.length) {
                    throw new java.util.NoSuchElementException();
                }
                return this.newArray[this.cursor++];
            }

            @Override
            public void remove() {
                throw new java.lang.UnsupportedOperationException();
            }
        };
    }

    private void resizeItemArray() {
        int newSize = this.itemArray.length;
        if (this.itemArray.length == this.size) {
            newSize = this.itemArray.length * 2;
        }
        if (this.size > 0 && this.itemArray.length / 4 == this.size) {
            newSize = this.itemArray.length / 2;
        }
        if (newSize != this.itemArray.length) {
            Item[] newItems = this.createEmptyArray(newSize);
            System.arraycopy(this.itemArray, 0, newItems, 0, size);
            this.itemArray = newItems;
        }
    }

    private Item[] copyArray(Item[] oldArray, int size) {
        Item[] newArray = this.createEmptyArray(size);
        System.arraycopy(oldArray, 0, newArray, 0, size);
        StdRandom.shuffle(newArray);
        return newArray;
    }

    private Item[] createEmptyArray(int size) {
        return (Item[]) new Object[size];
    }

    // unit testing (required)
    public static void main(String[] args) {
        RandomizedQueue<String> randomizedQueue = new RandomizedQueue<>();
        StdOut.println(randomizedQueue.size());
        StdOut.println(randomizedQueue.isEmpty());
        StdOut.println("=========");
        try {
            StdOut.println(randomizedQueue.sample());
        } catch (Exception e) {
            StdOut.println(e);
        }
        try {
            StdOut.println(randomizedQueue.dequeue());
        } catch (Exception e) {
            StdOut.println(e);
        }
        StdOut.println("=========");
        randomizedQueue.enqueue("First_1");
        randomizedQueue.enqueue("Last_1");
        randomizedQueue.enqueue("Last_2");
        randomizedQueue.enqueue("First_2");
        StdOut.println(randomizedQueue.size());
        StdOut.println(randomizedQueue.isEmpty());
        StdOut.println("=========");
        Iterator<String> iterator = randomizedQueue.iterator();
        while (iterator.hasNext()) {
            StdOut.println(iterator.next());
        }
        try {
            iterator.remove();
        } catch (Exception e) {
            StdOut.println(e);
        }
        StdOut.println("=========");
        Iterator<String> iterator2 = randomizedQueue.iterator();
        while (iterator2.hasNext()) {
            StdOut.println(iterator2.next());
        }
        StdOut.println("=========");
        try {
            StdOut.println(randomizedQueue.dequeue());
        } catch (Exception e) {
            StdOut.println(e);
        }
        try {
            StdOut.println(randomizedQueue.dequeue());
        } catch (Exception e) {
            StdOut.println(e);
        }
        StdOut.println("=========");
        StdOut.println(randomizedQueue.size());
        StdOut.println(randomizedQueue.isEmpty());
        StdOut.println("=========");
        try {
            StdOut.println(randomizedQueue.dequeue());
        } catch (Exception e) {
            StdOut.println(e);
        }
        try {
            StdOut.println(randomizedQueue.dequeue());
        } catch (Exception e) {
            StdOut.println(e);
        }
        try {
            StdOut.println(randomizedQueue.dequeue());
        } catch (Exception e) {
            StdOut.println(e);
        }
        try {
            StdOut.println(randomizedQueue.sample());
        } catch (Exception e) {
            StdOut.println(e);
        }
        StdOut.println("=========");
        StdOut.println(randomizedQueue.size());
        StdOut.println(randomizedQueue.isEmpty());
    }

}
