import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {

    private final int gridSize;
    private final boolean[] siteArray;
    private int numberOfOpenSites;
    private final WeightedQuickUnionUF quickUnionUF;

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be greater than 0.");
        }
        this.gridSize = n;
        this.siteArray = new boolean[n * n + 2];
        this.siteArray[0] = true;
        this.siteArray[n * n + 1] = true;
        this.numberOfOpenSites = 2;
        this.quickUnionUF = new WeightedQuickUnionUF(n * n + 2);
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        if (!this.isOpen(row, col)) {
            this.siteArray[this.getSiteValue(row, col)] = true;
            this.numberOfOpenSites++;
            if (row > 1 && this.isOpen(row - 1, col)) {
                this.quickUnionUF.union(this.getSiteValue(row - 1, col), this.getSiteValue(row, col));
            }
            if (row < this.gridSize && this.isOpen(row + 1, col)) {
                this.quickUnionUF.union(this.getSiteValue(row + 1, col), this.getSiteValue(row, col));
            }
            if (col > 1 && this.isOpen(row, col - 1)) {
                this.quickUnionUF.union(this.getSiteValue(row, col - 1), this.getSiteValue(row, col));
            }
            if (col < this.gridSize && this.isOpen(row, col + 1)) {
                this.quickUnionUF.union(this.getSiteValue(row, col + 1), this.getSiteValue(row, col));
            }
        }
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        if (row <= 0 || row > this.gridSize) {
            throw new IllegalArgumentException("row is out of range");
        }
        if (col <= 0 || col > this.gridSize) {
            throw new IllegalArgumentException("col is out of range");
        }
        return this.siteArray[this.getSiteValue(row, col)];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        if (!this.isOpen(row, col)) {
            return false;
        }
        return this.quickUnionUF.find(this.getSiteValue(row, col)) == this.quickUnionUF.find(0);
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return this.numberOfOpenSites;
    }

    // does the system percolate?
    public boolean percolates() {
        return this.quickUnionUF.find(this.gridSize * this.gridSize + 1) == this.quickUnionUF.find(0);
    }

    private int getSiteValue(int row, int col) {
        return (row - 1) * this.gridSize + col;
    }

    // test client (optional)
    public static void main(String[] args) {
    }
}
