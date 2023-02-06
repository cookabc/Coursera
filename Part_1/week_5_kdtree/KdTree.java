import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class KdTree {

    private int size;
    private Node root;

    // construct an empty set of points
    public KdTree() {
        this.root = null;
    }

    private static class Node {
        final Point2D point;
        final RectHV rect;
        Node left = null;
        Node right = null;

        Node(Point2D point, RectHV rect) {
            this.point = point;
            this.rect = rect;
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
        this.root = this.insert(this.root, p, 0, 0, 1, 1, Direction.HORIZONTAL);
    }

    private Node insert(Node node, Point2D p, double xmin, double ymin, double xmax, double ymax, Direction direction) {
        if (node == null) {
            this.size++;
            RectHV rect = new RectHV(xmin, ymin, xmax, ymax);
            return new Node(p, rect);
        }
        if (Objects.equals(p, node.point)) {
            return node;
        }
        switch (direction) {
            case HORIZONTAL:
                if (Point2D.X_ORDER.compare(p, node.point) < 0) {
                    node.left = this.insert(node.left, p, node.rect.xmin(), node.rect.ymin(), node.point.x(), node.rect.ymax(), Direction.VERTICAL);
                } else {
                    node.right = this.insert(node.right, p, node.point.x(), node.rect.ymin(), node.rect.xmax(), node.rect.ymax(), Direction.VERTICAL);
                }
                break;
            case VERTICAL:
                if (Point2D.Y_ORDER.compare(p, node.point) < 0) {
                    node.left = this.insert(node.left, p, node.rect.xmin(), node.rect.ymin(), node.rect.xmax(), node.point.y(), Direction.VERTICAL);
                } else {
                    node.right = this.insert(node.right, p, node.rect.xmin(), node.point.y(), node.rect.xmax(), node.rect.ymax(), Direction.VERTICAL);
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
        int compare = 0;
        Direction newDirection = direction;
        switch (direction) {
            case HORIZONTAL:
                compare = Point2D.X_ORDER.compare(p, node.point);
                newDirection = Direction.VERTICAL;
                break;
            case VERTICAL:
                compare = Point2D.Y_ORDER.compare(p, node.point);
                newDirection = Direction.HORIZONTAL;
                break;
        }
        if (compare < 0) {
            return this.search(node.left, p, newDirection);
        } else {
            return this.search(node.right, p, newDirection);
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
        List<Point2D> points = new ArrayList<>();
        this.range(this.root, rect, points);
        return points;
    }

    private void range(Node node, RectHV rect, List<Point2D> points) {
        if (node == null) {
            return;
        }
        if (!rect.intersects(node.rect)) {
            return;
        }
        if (rect.contains(node.point)) {
            points.add(node.point);
        }
        range(node.left, rect, points);
        range(node.right, rect, points);
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

    private Point2D nearest(Node node, Point2D p, Point2D nearestPoint, Direction direction) {
        if (node == null) {
            return nearestPoint;
        }
        if (node.point.equals(p)) {
            return node.point;
        }
        if (node.point.distanceSquaredTo(p) < nearestPoint.distanceSquaredTo(p)) {
            nearestPoint = node.point;
        }
        int compare = 0;
        Direction newDirection = direction;
        switch (direction) {
            case HORIZONTAL:
                compare = Point2D.X_ORDER.compare(p, node.point);
                newDirection = Direction.VERTICAL;
                break;
            case VERTICAL:
                compare = Point2D.Y_ORDER.compare(p, node.point);
                newDirection = Direction.HORIZONTAL;
                break;
        }
        if (compare < 0) {
            nearestPoint = this.nearest(node.left, p, nearestPoint, newDirection);
            if (node.right != null && nearestPoint.distanceSquaredTo(p) > node.right.rect.distanceSquaredTo(p)) {
                nearestPoint = this.nearest(node.right, p, nearestPoint, newDirection);
            }
        } else {
            nearestPoint = this.nearest(node.right, p, nearestPoint, newDirection);
            if (node.left != null && nearestPoint.distanceSquaredTo(p) > node.left.rect.distanceSquaredTo(p)) {
                nearestPoint = this.nearest(node.left, p, nearestPoint, newDirection);
            }
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
        System.out.println(kdTree.nearest(new Point2D(0.5, 0.5)));
        System.out.println("=============");
    }
}
