import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;

public class Solver {

    private SearchNode lastNode;

    private class SearchNode implements Comparable<SearchNode> {
        private Board board;
        private SearchNode pre;
        private int moves = 0;

        public SearchNode(Board board) {
            this.board = board;
        }

        public SearchNode(Board board, SearchNode pre) {
            this.board = board;
            this.pre = pre;
            this.moves = pre.moves + 1;
        }

        public int compareTo(SearchNode that) {
            return (this.board.manhattan() + this.moves) - (that.board.manhattan() + that.moves);
        }
    }

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        MinPQ<SearchNode> pq = new MinPQ<SearchNode>();
        pq.insert(new SearchNode(initial));

        MinPQ<SearchNode> pqTwin = new MinPQ<SearchNode>();
        pqTwin.insert(new SearchNode(initial.twin()));

        while (true) {
            lastNode = toSolve(pq);
            if (lastNode != null || toSolve(pqTwin) != null) {
                return;
            }
        }
    }

    private SearchNode toSolve(MinPQ<SearchNode> pq) {
        if (pq.isEmpty()) {
            return null;
        }

        SearchNode curNode = pq.delMin();

        if (curNode.board.isGoal()) {
            return curNode;
        }
        for (Board tmpBoard : curNode.board.neighbors()) {
            if (curNode.pre == null || !curNode.pre.board.equals(tmpBoard)) {
                pq.insert(new SearchNode(tmpBoard, curNode));
            }
        }
        return null;
    }

    // is the initial board solvable?
    public boolean isSolvable() {
        return lastNode != null;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        return isSolvable() ? lastNode.moves : -1;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        if (!isSolvable()) {
            return null;
        } else {
            Stack<Board> solutionStack = new Stack<>();
            SearchNode tmpNode = lastNode;
            while (tmpNode != null) {
                solutionStack.push(tmpNode.board);
                tmpNode = tmpNode.pre;
            }
            return solutionStack;
        }
    }

}