import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.util.Objects;

public class KdTree {

    private int size = 0;
    private Node root;

    // construct an empty set of points
    public KdTree() {
        this.root = null;
    }

    private static class Node {
        final Point2D point;
        Node left = null;
        Node right = null;

        Node(Point2D point) {
            this.point = point;
        }
    }

    private enum Direction {
        HORIZONTAL, VERTICAL
    }

    // is the set empty?
    public boolean isEmpty() {
        return this.root == null;
    }

    // number of points in the set
    public int size() {
        return this.size;
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        this.root = this.insert(this.root, p, Direction.HORIZONTAL);
    }

    private Node insert(Node node, Point2D p, Direction direction) {
        if (node == null) {
            this.size++;
            return new Node(p);
        }
        switch (direction) {
            case HORIZONTAL:
                if (p.x() < node.point.x()) {
                    node.left = this.insert(node.left, p, Direction.VERTICAL);
                } else if (p.x() > node.point.x()) {
                    node.right = this.insert(node.right, p, Direction.VERTICAL);
                }
            case VERTICAL:
                if (p.y() < node.point.y()) {
                    node.left = this.insert(node.left, p, Direction.HORIZONTAL);
                } else if (p.y() > node.point.y()) {
                    node.right = this.insert(node.right, p, Direction.HORIZONTAL);
                }
        }
        return node;
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        return this.search(this.root, p, Direction.HORIZONTAL);
    }

    private boolean search(Node node, Point2D p, Direction direction) {
        if (node == null) {
            return false;
        }
        if (Objects.equals(p, node.point)) {
            return true;
        }
        switch (direction) {
            case HORIZONTAL:
                if (p.x() < node.point.x()) {
                    return this.search(node.left, p, Direction.VERTICAL);
                } else if (p.x() > node.point.x()) {
                    return this.search(node.right, p, Direction.VERTICAL);
                }
            case VERTICAL:
                if (p.y() < node.point.y()) {
                    return this.search(node.left, p, Direction.HORIZONTAL);
                } else if (p.y() > node.point.y()) {
                    return this.search(node.right, p, Direction.HORIZONTAL);
                }
            default:
                return false;
        }
    }

    // draw all points to standard draw
    public void draw() {
        this.draw(this.root);
    }

    private void draw(Node node) {
        if (node == null) {
            return;
        }
        node.point.draw();
        this.draw(node.left);
        this.draw(node.right);
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException();
        }
        return null;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        return p;
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        KdTree kdTree = new KdTree();
        kdTree.insert(new Point2D(0.7, 0.5));
        kdTree.insert(new Point2D(0.1, 0.2));
        kdTree.insert(new Point2D(0.3, 0.4));
        System.out.println(kdTree.contains(new Point2D(0.1, 0.2)));
        System.out.println(kdTree.contains(new Point2D(0.3, 0.3)));
        System.out.println(kdTree.contains(new Point2D(0.7, 0.5)));
        StdDraw.setPenRadius(0.03);
        kdTree.draw();
    }
}
