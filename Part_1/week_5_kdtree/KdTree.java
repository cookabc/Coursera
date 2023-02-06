import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

import java.util.ArrayList;
import java.util.List;
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
                break;
            case VERTICAL:
                if (p.y() < node.point.y()) {
                    node.left = this.insert(node.left, p, Direction.HORIZONTAL);
                } else if (p.y() > node.point.y()) {
                    node.right = this.insert(node.right, p, Direction.HORIZONTAL);
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
        boolean result = false;
        switch (direction) {
            case HORIZONTAL:
                if (p.x() < node.point.x()) {
                    result = this.search(node.left, p, Direction.VERTICAL);
                } else if (p.x() > node.point.x()) {
                    result = this.search(node.right, p, Direction.VERTICAL);
                }
            case VERTICAL:
                if (p.y() < node.point.y()) {
                    result = this.search(node.left, p, Direction.HORIZONTAL);
                } else if (p.y() > node.point.y()) {
                    result = this.search(node.right, p, Direction.HORIZONTAL);
                }
        }
        return result;
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
        List<Point2D> points = new ArrayList<>();
        this.range(this.root, rect, Direction.HORIZONTAL, points);
        return points;
    }

    private void range(Node node, RectHV rect, Direction direction, List<Point2D> points) {
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
        return this.nearest(this.root, p, Direction.HORIZONTAL);
    }

    private Point2D nearest(Node node, Point2D queryPoint, Direction direction) {
        if (node == null) {
            return null;
        }
        Point2D nearestPoint = node.point;
        Point2D nearest;
        switch (direction) {
            case HORIZONTAL:
                if (queryPoint.x() < node.point.x()) {
                    nearest = this.nearest(node.left, queryPoint, Direction.VERTICAL);
                } else {
                    nearest = this.nearest(node.right, queryPoint, Direction.VERTICAL);
                }
                if (nearest != null && nearest.distanceSquaredTo(queryPoint) < nearestPoint.distanceSquaredTo(queryPoint)) {
                    nearestPoint = nearest;
                }
                break;
            case VERTICAL:
                if (queryPoint.y() < node.point.y()) {
                    nearest = this.nearest(node.left, queryPoint, Direction.HORIZONTAL);
                } else {
                    nearest = this.nearest(node.right, queryPoint, Direction.HORIZONTAL);
                }
                if (nearest != null && nearest.distanceSquaredTo(queryPoint) < nearestPoint.distanceSquaredTo(queryPoint)) {
                    nearestPoint = nearest;
                }
                nearest = this.nearest(node.right, queryPoint, Direction.HORIZONTAL);
                if (nearest != null && nearest.distanceTo(queryPoint) < nearestPoint.distanceTo(queryPoint)) {
                    nearestPoint = nearest;
                }
                break;
        }
        return nearestPoint;
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        KdTree kdTree = new KdTree();
        kdTree.insert(new Point2D(0.4, 0.3));
        kdTree.insert(new Point2D(0.7, 0.5));
        kdTree.insert(new Point2D(0.1, 0.4));
        kdTree.insert(new Point2D(0.3, 0.3));
        System.out.println(kdTree.contains(new Point2D(0.1, 0.2)));
        System.out.println(kdTree.contains(new Point2D(0.3, 0.3)));
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
