import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {

    private final int trials;
    private final double[] thresholdArray;

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        if (n <= 0 || trials <= 0) {
            throw new IllegalArgumentException("n or trails must be greater than 0.");
        }
        this.trials = trials;
        this.thresholdArray = new double[this.trials];
        for (int i = 0; i < this.trials; i++) {
            Percolation percolation = new Percolation(n);
            this.thresholdArray[i] = (double) percolation.numberOfOpenSites() / (n * n);
        }
    }

    // sample mean of percolation threshold
    public double mean() {
        return StdStats.mean(this.thresholdArray);
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return StdStats.stddev(this.thresholdArray);
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return this.mean() - 1.96 * this.stddev() / Math.sqrt(this.trials);
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return this.mean() + 1.96 * this.stddev() / Math.sqrt(this.trials);
    }

    // test client (see below)
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int trials = Integer.parseInt(args[1]);
        PercolationStats perStats = new PercolationStats(n, trials);

        StdOut.println("mean\t\t\t= " + perStats.mean());
        StdOut.println("stddev\t\t\t= " + perStats.stddev());
        StdOut.println("95% confidence interval\t= " + perStats.confidenceLo() + ", " + perStats.confidenceHi());
    }
}
