import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;

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
        if (Objects.equals(p, node.point)) {
            return node;
        }
        switch (direction) {
            case HORIZONTAL:
                if (Point2D.X_ORDER.compare(p, node.point) < 0) {
                    node.left = this.insert(node.left, p, Direction.VERTICAL);
                } else {
                    node.right = this.insert(node.right, p, Direction.VERTICAL);
                }
                break;
            case VERTICAL:
                if (Point2D.Y_ORDER.compare(p, node.point) < 0) {
                    node.left = this.insert(node.left, p, Direction.VERTICAL);
                } else {
                    node.right = this.insert(node.right, p, Direction.VERTICAL);
                }
                break;
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
                if (Point2D.X_ORDER.compare(p, node.point) < 0) {
                    return this.search(node.left, p, Direction.VERTICAL);
                } else {
                    return this.search(node.right, p, Direction.VERTICAL);
                }
            case VERTICAL:
                if (Point2D.Y_ORDER.compare(p, node.point) < 0) {
                    return this.search(node.left, p, Direction.HORIZONTAL);
                } else {
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
        SET<Point2D> points = new SET<>();
        this.range(this.root, rect, Direction.HORIZONTAL, points);
        return points;
    }

    private void range(Node node, RectHV rect, Direction direction, SET<Point2D> points) {
        if (node == null) {
            return;
        }
        if (rect.contains(node.point)) {
            points.add(node.point);
        }
        switch (direction) {
            case HORIZONTAL:
                if (node.point.x() <= rect.xmin()) {
                    range(node.right, rect, Direction.VERTICAL, points);
                } else if (node.point.x() < rect.xmax()) {
                    range(node.left, rect, Direction.VERTICAL, points);
                    range(node.right, rect, Direction.VERTICAL, points);
                } else if (node.point.x() >= rect.xmax()) {
                    range(node.left, rect, Direction.VERTICAL, points);
                }
                break;
            case VERTICAL:
                if (node.point.y() <= rect.ymin()) {
                    range(node.right, rect, Direction.HORIZONTAL, points);
                } else if (node.point.y() < rect.ymax()) {
                    range(node.left, rect, Direction.HORIZONTAL, points);
                    range(node.right, rect, Direction.HORIZONTAL, points);
                } else if (node.point.y() >= rect.ymax()) {
                    range(node.left, rect, Direction.HORIZONTAL, points);
                }
                break;
        }
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        if (this.isEmpty()) {
            return null;
        }
        return this.nearest(this.root, p, this.root.point, Direction.HORIZONTAL);
    }

    private Point2D nearest(Node node, Point2D queryPoint, Point2D nearestPoint, Direction direction) {
        if (node == null) {
            return nearestPoint;
        }
        if (node.point.equals(queryPoint)) {
            return node.point;
        }
        if (node.point.distanceSquaredTo(queryPoint) < nearestPoint.distanceSquaredTo(queryPoint)) {
            nearestPoint = node.point;
        }
        switch (direction) {
            case HORIZONTAL:
                if (Point2D.X_ORDER.compare(queryPoint, node.point) < 0) {
                    nearestPoint = this.nearest(node.left, queryPoint, nearestPoint, Direction.VERTICAL);
                } else {
                    nearestPoint = this.nearest(node.right, queryPoint, nearestPoint, Direction.VERTICAL);
                }
                break;
            case VERTICAL:
                if (Point2D.Y_ORDER.compare(queryPoint, node.point) < 0) {
                    nearestPoint = this.nearest(node.left, queryPoint, nearestPoint, Direction.HORIZONTAL);
                } else {
                    nearestPoint = this.nearest(node.right, queryPoint, nearestPoint, Direction.HORIZONTAL);
                }
                break;
        }
        return nearestPoint;
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        KdTree kdTree = new KdTree();
        kdTree.insert(new Point2D(0.29, 0.17));
        kdTree.insert(new Point2D(0.6, 0.87));
        kdTree.insert(new Point2D(0.37, 0.01));
        kdTree.insert(new Point2D(0.9, 0.61));
        kdTree.insert(new Point2D(0.37, 0.01));
        kdTree.insert(new Point2D(0.75, 0.23));
        kdTree.insert(new Point2D(0.58, 0.31));
        kdTree.insert(new Point2D(0.69, 0.43));
        kdTree.insert(new Point2D(0.57, 0.69));
        kdTree.insert(new Point2D(0.69, 0.85));
        kdTree.insert(new Point2D(0.88, 0.9));
        kdTree.insert(new Point2D(0.04, 0.09));
        System.out.println(kdTree.size());
        System.out.println(kdTree.contains(new Point2D(0.1, 0.2)));
        System.out.println(kdTree.contains(new Point2D(0.04, 0.09)));
        System.out.println(kdTree.contains(new Point2D(0.7, 0.7)));
        System.out.println("=============");
        Iterable<Point2D> range = kdTree.range(new RectHV(0.0, 0.0, 1, 1));
        range.forEach(System.out::println);
        System.out.println("=============");
        range = kdTree.range(new RectHV(0.0, 0.0, 0.5, 0.5));
        range.forEach(System.out::println);
        System.out.println("=============");
        System.out.println(kdTree.nearest(new Point2D(0.1, 0.1)));
        System.out.println("=============");
    }
}
