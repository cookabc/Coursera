import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class Permutation {
    public static void main(String[] args) {
        RandomizedQueue<String> rq = new RandomizedQueue<>();
        while (!StdIn.isEmpty()) {
            String s = StdIn.readString();
            rq.enqueue(s);
        }
        int n = Integer.parseInt(args[0]);
        for (int i = 0; i < n; i++) {
            StdOut.println(rq.dequeue());
        }
    }
}
