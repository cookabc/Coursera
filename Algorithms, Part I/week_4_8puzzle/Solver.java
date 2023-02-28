import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.LinkedStack;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;

import java.util.Objects;

public class Solver {

    private SearchBoard goalBoard = null;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) {
            throw new IllegalArgumentException("initial board cannot be null");
        }

        MinPQ<SearchBoard> minPQ = new MinPQ<>();
        SearchBoard minSearchBoard = new SearchBoard(initial);

        MinPQ<SearchBoard> minPQTwin = new MinPQ<>();
        SearchBoard minSearchBoardTwin = new SearchBoard(initial.twin());

        while (!minSearchBoard.board.isGoal() && !minSearchBoardTwin.board.isGoal()) {
            minSearchBoard = getSearchBoard(minPQ, minSearchBoard);
            minSearchBoardTwin = getSearchBoard(minPQTwin, minSearchBoardTwin);
        }
        if (minSearchBoard.board.isGoal()) {
            this.goalBoard = minSearchBoard;
        }
    }

    private static SearchBoard getSearchBoard(MinPQ<SearchBoard> minPQ, SearchBoard minSearchBoard) {
        for (Board neighborBoard : minSearchBoard.board.neighbors()) {
            if (minSearchBoard.prev == null || !Objects.equals(neighborBoard, minSearchBoard.prev.board)) {
                minPQ.insert(new SearchBoard(neighborBoard, minSearchBoard));
            }
        }
        return minPQ.isEmpty() ? minSearchBoard : minPQ.delMin();
    }

    private static class SearchBoard implements Comparable<SearchBoard> {

        private final Board board;
        private int moves = 0;
        private SearchBoard prev = null;

        public SearchBoard(Board board) {
            this.board = board;
        }

        public SearchBoard(Board board, SearchBoard prev) {
            this.board = board;
            this.moves = prev.moves + 1;
            this.prev = prev;
        }

        @Override
        public int compareTo(SearchBoard that) {
            return this.board.manhattan() + this.moves - that.board.manhattan() - that.moves;
        }
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return this.goalBoard != null;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        return this.isSolvable() ? this.goalBoard.moves : -1;
    }

    // sequence of boards in one shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        if (this.isSolvable()) {
            LinkedStack<Board> solutionBoards = new LinkedStack<>();
            SearchBoard cursorBoard = this.goalBoard;
            while (cursorBoard != null) {
                solutionBoards.push(cursorBoard.board);
                cursorBoard = cursorBoard.prev;
            }
            return solutionBoards;
        }
        return null;
    }

    // test client (see below)
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}
