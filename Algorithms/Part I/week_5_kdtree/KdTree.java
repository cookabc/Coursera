import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

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
        RectHV rect;
        Node left;
        Node right;

        Node(Point2D point, RectHV rect) {
            this.point = point;
            this.rect = rect;
        }
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
        this.root = this.insert(this.root, p, 0, 0, 1, 1, true);
    }

    private Node insert(Node node, Point2D p, double xmin, double ymin, double xmax, double ymax, boolean direction) {
        if (node == null) {
            this.size++;
            return new Node(p, new RectHV(xmin, ymin, xmax, ymax));
        }
        if (Objects.equals(p, node.point)) {
            return node;
        }
        if (this.compare(p, node.point, direction) < 0) {
            if (direction) {
                node.left = this.insert(node.left, p, node.rect.xmin(), node.rect.ymin(), node.point.x(), node.rect.ymax(), false);
            } else {
                node.left = this.insert(node.left, p, node.rect.xmin(), node.rect.ymin(), node.rect.xmax(), node.point.y(), true);
            }
        } else {
            if (direction) {
                node.right = this.insert(node.right, p, node.point.x(), node.rect.ymin(), node.rect.xmax(), node.rect.ymax(), false);
            } else {
                node.right = this.insert(node.right, p, node.rect.xmin(), node.point.y(), node.rect.xmax(), node.rect.ymax(), true);
            }
        }
        return node;
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        return this.contains(this.root, p, true);
    }

    private boolean contains(Node node, Point2D p, boolean direction) {
        if (node == null) {
            return false;
        }
        if (Objects.equals(p, node.point)) {
            return true;
        }
        if (this.compare(p, node.point, direction) < 0) {
            return this.contains(node.left, p, !direction);
        } else {
            return this.contains(node.right, p, !direction);
        }
    }

    // draw all points to standard draw
    public void draw() {
        this.draw(this.root, true);
    }

    private void draw(Node node, boolean direction) {
        if (node == null) {
            return;
        }
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        node.point.draw();
        StdDraw.setPenRadius();
        if (direction) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(node.point.x(), node.rect.ymin(), node.point.x(), node.rect.ymax());
        } else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(node.rect.xmin(), node.point.y(), node.rect.xmax(), node.point.y());
        }
        this.draw(node.left, !direction);
        this.draw(node.right, !direction);
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
        return this.nearest(this.root, p, this.root.point, true);
    }

    private Point2D nearest(Node node, Point2D p, Point2D nearestPoint, boolean direction) {
        if (node == null) {
            return nearestPoint;
        }
        if (node.point.equals(p)) {
            return node.point;
        }
        if (node.point.distanceSquaredTo(p) < nearestPoint.distanceSquaredTo(p)) {
            nearestPoint = node.point;
        }
        if (this.compare(p, node.point, direction) < 0) {
            nearestPoint = this.nearest(node.left, p, nearestPoint, !direction);
            if (node.right != null && nearestPoint.distanceSquaredTo(p) > node.right.rect.distanceSquaredTo(p)) {
                nearestPoint = this.nearest(node.right, p, nearestPoint, !direction);
            }
        } else {
            nearestPoint = this.nearest(node.right, p, nearestPoint, !direction);
            if (node.left != null && nearestPoint.distanceSquaredTo(p) > node.left.rect.distanceSquaredTo(p)) {
                nearestPoint = this.nearest(node.left, p, nearestPoint, !direction);
            }
        }
        return nearestPoint;
    }

    private int compare(Point2D p, Point2D q, boolean even) {
        if (even) {
            return Point2D.X_ORDER.compare(p, q);
        } else {
            return Point2D.Y_ORDER.compare(p, q);
        }
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
        kdTree.draw();
    }
}
