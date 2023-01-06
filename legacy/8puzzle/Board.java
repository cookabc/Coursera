import java.util.Arrays;
import java.util.LinkedList;

public class Board {

    private int[][] blocks;
    private int dimension;

    // construct a board from an n-by-n array of blocks
    // (where blocks[i][j] = block in row i, column j)
    public Board(int[][] blocks) {
        this.dimension = blocks.length;
        this.blocks = new int[dimension][];
        for (int k = 0; k < dimension; k++) {
            this.blocks[k] = Arrays.copyOf(blocks[k], dimension);
        }
    }

    // board dimension n
    public int dimension() {
        return dimension;
    }

    // number of blocks out of place
    public int hamming() {
        int count = 0;
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension && i + j < dimension * 2 - 2; j++) {
                if (blocks[i][j] != i * dimension + j + 1) {
                    count++;
                }
            }
        }
        return count;
    }

    // sum of Manhattan distances between blocks and goal
    public int manhattan() {
        int total = 0;
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (blocks[i][j] != 0) {
                    total += Math.abs((blocks[i][j] - 1) % dimension - j)
                            + Math.abs((blocks[i][j] - 1) / dimension - i);
                }
            }
        }
        return total;
    }

    // is this board the goal board?
    public boolean isGoal() {
        return this.manhattan() == 0;
    }

    // a board that is obtained by exchanging any pair of blocks
    public Board twin() {
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension - 1; j++) {
                if (blocks[i][j] != 0 && blocks[i][j + 1] != 0) {
                    return exch(i, j, i, j + 1);
                }
            }
        }
        return this;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (y == this) {
            return true;
        }
        if (y == null || y.getClass() != this.getClass() || ((Board) y).dimension() != dimension) {
            return false;
        }
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (blocks[i][j] != ((Board) y).blocks[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        LinkedList<Board> neighborList = new LinkedList<>();
        int zeroX = dimension - 1;
        int zeroY = dimension - 1;
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (blocks[i][j] == 0) {
                    zeroX = i;
                    zeroY = j;
                }
            }
        }
        if (zeroX > 0) {
            neighborList.add(exch(zeroX, zeroY, zeroX - 1, zeroY));
        }
        if (zeroX < dimension - 1) {
            neighborList.add(exch(zeroX, zeroY, zeroX + 1, zeroY));
        }
        if (zeroY > 0) {
            neighborList.add(exch(zeroX, zeroY, zeroX, zeroY - 1));
        }
        if (zeroY < dimension - 1) {
            neighborList.add(exch(zeroX, zeroY, zeroX, zeroY + 1));
        }
        return neighborList;
    }

    private Board exch(int i, int j, int i2, int j2) {
        int[][] tmp = new int[dimension][];
        for (int k = 0; k < dimension; k++) {
            tmp[k] = Arrays.copyOf(blocks[k], dimension);
        }
        int temp = tmp[i][j];
        tmp[i][j] = tmp[i2][j2];
        tmp[i2][j2] = temp;
        return new Board(tmp);
    }

    // string representation of this board (in the output format specified
    // below)
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(dimension + "\n");
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                s.append(String.format("%2d", blocks[i][j]));
                s.append(" ");
            }
            s.append("\n");
        }
        return s.toString();
    }

}