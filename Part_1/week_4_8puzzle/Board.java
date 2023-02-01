import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Board {

    private final int dimension;
    private final int[][] tiles;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        this.dimension = tiles.length;
        this.tiles = new int[this.dimension][this.dimension];
        for (int i = 0; i < this.dimension; i++) {
            this.tiles[i] = Arrays.copyOf(tiles[i], this.dimension);
        }
    }

    // string representation of this board
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("\n");
        s.append(this.dimension);
        for (int i = 0; i < this.dimension; i++) {
            s.append("\n");
            for (int j = 0; j < this.dimension; j++) {
                s.append(String.format("%2d", this.tiles[i][j])).append(" ");
            }
        }
        return s.toString();
    }

    // board dimension n
    public int dimension() {
        return this.dimension;
    }

    // number of tiles out of place
    public int hamming() {
        int outPlaceCount = 0;
        for (int i = 0; i < this.dimension; i++) {
            for (int j = 0; j < this.dimension; j++) {
                if (this.tiles[i][j] != 0 && this.tiles[i][j] != (i * this.dimension + j + 1)) {
                    outPlaceCount++;
                }
            }
        }
        return outPlaceCount;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        int distances = 0;
        for (int i = 0; i < this.dimension; i++) {
            for (int j = 0; j < this.dimension; j++) {
                if (this.tiles[i][j] != 0) {
                    int horizontalDistances = Math.abs((this.tiles[i][j] - 1) / this.dimension - i);
                    int verticalDistances = Math.abs((this.tiles[i][j] - 1) % this.dimension - j);
                    distances += (horizontalDistances + verticalDistances);
                }
            }
        }
        return distances;
    }

    // is this board the goal board?
    public boolean isGoal() {
        return this.hamming() == 0;
    }

    // does this board equal y?
    @Override
    public boolean equals(Object y) {
        if (this == y) return true;
        if (y == null || this.getClass() != y.getClass()) return false;

        Board anotherBoard = (Board) y;
        if (dimension != anotherBoard.dimension) return false;
        return Arrays.deepEquals(this.tiles, anotherBoard.tiles);
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        List<Board> neighbors = new LinkedList<>();
        int zeroX = this.dimension - 1;
        int zeroY = this.dimension - 1;
        for (int i = 0; i < this.dimension; i++) {
            if (this.tiles[zeroX][zeroY] == 0) {
                break;
            }
            for (int j = 0; j < this.dimension; j++) {
                if (this.tiles[i][j] == 0) {
                    zeroX = i;
                    zeroY = j;
                    break;
                }
            }
        }
        if (zeroX > 0) {
            neighbors.add(this.exchange(zeroX, zeroY, zeroX - 1, zeroY));
        }
        if (zeroX < this.dimension - 1) {
            neighbors.add(this.exchange(zeroX, zeroY, zeroX + 1, zeroY));
        }
        if (zeroY > 0) {
            neighbors.add(this.exchange(zeroX, zeroY, zeroX, zeroY - 1));
        }
        if (zeroY < this.dimension - 1) {
            neighbors.add(this.exchange(zeroX, zeroY, zeroX, zeroY + 1));
        }
        return neighbors;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        int row = 0;
        int col = 0;
        while (row == col || this.tiles[row][col] == 0 || this.tiles[col][row] == 0) {
            row = StdRandom.uniformInt(this.dimension);
            col = StdRandom.uniformInt(this.dimension);
        }
        return this.exchange(row, col, col, row);
    }

    private Board exchange(int srcX, int srcY, int destX, int destY) {
        int[][] newTiles = new int[this.dimension][];
        for (int i = 0; i < this.dimension; i++) {
            newTiles[i] = Arrays.copyOf(this.tiles[i], this.dimension);
        }
        int temp = newTiles[srcX][srcY];
        newTiles[srcX][srcY] = newTiles[destX][destY];
        newTiles[destX][destY] = temp;
        return new Board(newTiles);
    }

    // unit testing (not graded)
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                tiles[i][j] = in.readInt();
            }
        }
        Board board = new Board(tiles);
        StdOut.println(board);
        StdOut.println("=============");
        StdOut.println(board.dimension());
        StdOut.println("=============");
        StdOut.println(board.hamming());
        StdOut.println(board.manhattan());
        StdOut.println(board.isGoal());
        StdOut.println("=============");
        for (Board value : board.neighbors()) {
            StdOut.println(value);
        }
        StdOut.println("=============");
        Board twin = board.twin();
        StdOut.println(twin);
        StdOut.println(board.equals(twin));
        StdOut.println(board);
        StdOut.println("=============");
    }
}
