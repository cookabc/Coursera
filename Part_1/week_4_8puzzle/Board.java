import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.util.ArrayList;
import java.util.Arrays;
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
                if (this.tiles[i][j] == 0) {
                    continue;
                }
                if (this.tiles[i][j] != (i * this.dimension + j + 1)) {
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
                if (this.tiles[i][j] == 0) {
                    continue;
                }
                int horizontalDistances = Math.abs(this.tiles[i][j] / this.dimension - i);
                int verticalDistances = Math.abs((this.tiles[i][j] - 1) % this.dimension - j);
                distances += (horizontalDistances + verticalDistances);
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
        List<Board> neighbors = new ArrayList<>();
        for (int i = 0; i < this.tiles.length; i++) {
            for (int j = 0; j < this.tiles.length; j++) {
                if (this.tiles[i][j] == 0) {
                    if (i > 0) {
                        int[][] leftBoardTiles = new int[this.dimension][this.dimension];
                        for (int k = 0; k < this.dimension; k++) {
                            leftBoardTiles[k] = Arrays.copyOf(tiles[k], this.dimension);
                        }
                        leftBoardTiles[i][j] = this.tiles[i - 1][j];
                        leftBoardTiles[i - 1][j] = 0;
                        neighbors.add(new Board(leftBoardTiles));
                    }

                    if (i < this.dimension - 1) {
                        int[][] rightBoardTiles = new int[this.dimension][this.dimension];
                        for (int k = 0; k < this.dimension; k++) {
                            rightBoardTiles[k] = Arrays.copyOf(tiles[k], this.dimension);
                        }
                        rightBoardTiles[i][j] = this.tiles[i + 1][j];
                        rightBoardTiles[i + 1][j] = 0;
                        neighbors.add(new Board(rightBoardTiles));
                    }

                    if (j > 0) {
                        int[][] topBoardTiles = new int[this.dimension][this.dimension];
                        for (int k = 0; k < this.dimension; k++) {
                            topBoardTiles[k] = Arrays.copyOf(tiles[k], this.dimension);
                        }
                        topBoardTiles[i][j] = this.tiles[i][j - 1];
                        topBoardTiles[i][j - 1] = 0;
                        neighbors.add(new Board(topBoardTiles));
                    }

                    if (j < this.dimension - 1) {
                        int[][] bottomBoardTiles = new int[this.dimension][this.dimension];
                        for (int k = 0; k < this.dimension; k++) {
                            bottomBoardTiles[k] = Arrays.copyOf(tiles[k], this.dimension);
                        }
                        bottomBoardTiles[i][j] = this.tiles[i][j + 1];
                        bottomBoardTiles[i][j + 1] = 0;
                        neighbors.add(new Board(bottomBoardTiles));
                    }
                    break;
                }
            }
            if (neighbors.size() > 0) {
                break;
            }
        }
        return neighbors;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        int row = 0;
        int col = 0;
        while (row == col) {
            row = StdRandom.uniformInt(this.dimension);
            col = StdRandom.uniformInt(this.dimension);
        }
        int[][] twinTiles = new int[this.dimension][this.dimension];
        for (int i = 0; i < this.dimension; i++) {
            twinTiles[i] = Arrays.copyOf(this.tiles[i], this.dimension);
        }
        int tmp = twinTiles[row][col];
        twinTiles[row][col] = this.tiles[col][row];
        twinTiles[col][row] = tmp;
        return new Board(twinTiles);
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
