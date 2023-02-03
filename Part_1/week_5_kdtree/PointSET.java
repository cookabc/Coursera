import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;

import java.util.ArrayList;
import java.util.List;

public class PointSET {

    private final SET<Point2D> pointSet;

    // construct an empty set of points
    public PointSET() {
        this.pointSet = new SET<>();
    }

    // is the set empty?
    public boolean isEmpty() {
        return this.pointSet.isEmpty();
    }

    // number of points in the set
    public int size() {
        return this.pointSet.size();
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        if (!this.contains(p)) {
            this.pointSet.add(p);
        }
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        return this.pointSet.contains(p);
    }

    // draw all points to standard draw
    public void draw() {
        this.pointSet.forEach(Point2D::draw);
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException();
        }
        List<Point2D> points = new ArrayList<>();
        this.pointSet.forEach(p -> {
            if (rect.xmin() <= p.x() && rect.xmax() >= p.x() && rect.ymin() <= p.y() && rect.ymax() >= p.y()) {
                points.add(p);
            }
        });
        return points;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        if (this.isEmpty()) {
            return null;
        }
        double distance = Double.MAX_VALUE;
        Point2D nearestNeighbor = null;
        for (Point2D q : this.pointSet) {
            if (q.distanceTo(p) < distance) {
                distance = q.distanceTo(p);
                nearestNeighbor = q;
            }
        }
        return nearestNeighbor;
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
    }
}
