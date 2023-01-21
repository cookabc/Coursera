import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class RandomWord {
    public static void main(String[] args) {
        int i = 1;
        String champion = StdIn.readString();
        while (!StdIn.isEmpty()) {
            i++;
            String token = StdIn.readString();
            if (StdRandom.bernoulli(1D / i)) {
                champion = token;
            }
        }
        StdOut.println(champion);
    }
}
