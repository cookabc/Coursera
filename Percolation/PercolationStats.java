import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {

    private double[] thresholds;
    private int expCount;

    public PercolationStats(int n, int trials) {
        if (n <= 0 || trials <= 0) {
            throw new IllegalArgumentException("Wrong Number!");
        }

        expCount = trials;
        thresholds = new double[expCount];
        for (int t = 0; t < expCount; t++) {
            Percolation per = new Percolation(n);
            while (!per.percolates()) {
                int row = StdRandom.uniform(1, n + 1);
                int col = StdRandom.uniform(1, n + 1);
                if (!per.isOpen(row, col)) {
                    per.open(row, col);
                }
            }
            double threshold = (double) per.numberOfOpenSites() / (n * n);
            thresholds[t] = threshold;
        }
    }

    // sample mean of percolation threshold
    public double mean() {
        return StdStats.mean(thresholds);

    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return StdStats.stddev(thresholds);

    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return mean() - 1.96 * stddev() / Math.sqrt(expCount);

    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return mean() + 1.96 * stddev() / Math.sqrt(expCount);

    }

    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int trials = Integer.parseInt(args[1]);
        PercolationStats perStats = new PercolationStats(n, trials);

        StdOut.println("mean\t\t\t= " + perStats.mean());
        StdOut.println("stddev\t\t\t= " + perStats.stddev());
        StdOut.println("95% confidence interval\t= " + perStats.confidenceLo() + ", " + perStats.confidenceHi());
    }

}
