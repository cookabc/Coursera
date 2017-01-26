import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {

    private boolean[][] grid;
    private int sz; // grid_size;
    private int countOpenSites = 0;
    private int top = 0;
    private int bottom;
    private WeightedQuickUnionUF wqu;
    private WeightedQuickUnionUF wquNoBottom;

    // create n-by-n grid, with all sites blocked
    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException();
        }
        sz = n;
        bottom = sz * sz + 1;
        grid = new boolean[sz][sz];
        wqu = new WeightedQuickUnionUF(n * n + 2);
        wquNoBottom = new WeightedQuickUnionUF(n * n + 1);
    }

    private int getGridIndex(int row, int col) {
        return (row - 1) * sz + col;
    }

    // open site (row, col) if it is not open already
    public void open(int row, int col) {
        if (!isOpen(row, col)) {
            grid[row - 1][col - 1] = true;
            countOpenSites++;

            if (row == 1) {
                wqu.union(getGridIndex(row, col), top);
                wquNoBottom.union(getGridIndex(row, col), top);
            }
            if (row == sz) {
                wqu.union(getGridIndex(row, col), bottom);
            }

            if (row > 1 && isOpen(row - 1, col)) {
                wqu.union(getGridIndex(row, col), getGridIndex(row - 1, col));
                wquNoBottom.union(getGridIndex(row, col), getGridIndex(row - 1, col));
            }
            if (row < sz && isOpen(row + 1, col)) {
                wqu.union(getGridIndex(row, col), getGridIndex(row + 1, col));
                wquNoBottom.union(getGridIndex(row, col), getGridIndex(row + 1, col));
            }
            if (col > 1 && isOpen(row, col - 1)) {
                wqu.union(getGridIndex(row, col), getGridIndex(row, col - 1));
                wquNoBottom.union(getGridIndex(row, col), getGridIndex(row, col - 1));
            }
            if (col < sz && isOpen(row, col + 1)) {
                wqu.union(getGridIndex(row, col), getGridIndex(row, col + 1));
                wquNoBottom.union(getGridIndex(row, col), getGridIndex(row, col + 1));
            }
        }
    }

    // is site (row, col) open?
    public boolean isOpen(int row, int col) {
        if (0 < row && row <= sz && 0 < col && col <= sz) {
            return grid[row - 1][col - 1];
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    // is site (row, col) full?
    public boolean isFull(int row, int col) {
        if (0 < row && row <= sz && 0 < col && col <= sz) {
            return wquNoBottom.connected(top, getGridIndex(row, col));
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    // number of open sites
    public int numberOfOpenSites() {
        return countOpenSites;
    }

    // does the system percolate?
    public boolean percolates() {
        return wqu.connected(top, bottom);
    }

}
