import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdDraw;

public class KdTree {

    private class Node {
        private Point2D point;
        private RectHV rect;
        private Node left = null;
        private Node right = null;

        public Node(Point2D p, RectHV rect) {
            this.point = p;
            this.rect = rect;
        }
    }

    private Node rootNode;
    private int treeSize;

    // construct an empty set of points
    public KdTree() {
        rootNode = null;
        treeSize = 0;
    }

    // is the set empty?
    public boolean isEmpty() {
        return rootNode == null;
    }

    // number of points in the set
    public int size() {
        return treeSize;
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        checkIfArgIsNull(p);
        if (rootNode == null) {
            RectHV rect = new RectHV(0, 0, 1, 1);
            rootNode = new Node(p, rect);
            treeSize++;
        }
        rootNode = insert(rootNode, 0, 0, 1, 1, p, true);
    }

    private Node insert(Node cur, double xmin, double ymin, double xmax, double ymax, Point2D p, boolean vertical) {
        if (cur == null) {
            treeSize++;
            return new Node(p, new RectHV(xmin, ymin, xmax, ymax));
        }
        if (!cur.point.equals(p)) {
            int cmp;
            if (vertical) {
                cmp = Point2D.X_ORDER.compare(p, cur.point);
                if (cmp < 0) {
                    cur.left = insert(cur.left, cur.rect.xmin(), cur.rect.ymin(), cur.point.x(), cur.rect.ymax(), p,
                            !vertical);
                } else {
                    cur.right = insert(cur.right, cur.point.x(), cur.rect.ymin(), cur.rect.xmax(), cur.rect.ymax(), p,
                            !vertical);
                }
            } else {
                cmp = Point2D.Y_ORDER.compare(p, cur.point);
                if (cmp < 0) {
                    cur.left = insert(cur.left, cur.rect.xmin(), cur.rect.ymin(), cur.rect.xmax(), cur.point.y(), p,
                            !vertical);
                } else {
                    cur.right = insert(cur.right, cur.rect.xmin(), cur.point.y(), cur.rect.xmax(), cur.rect.ymax(), p,
                            !vertical);
                }
            }
        }
        return cur;
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        checkIfArgIsNull(p);
        return search(rootNode, p, true);
    }

    private boolean search(Node cur, Point2D p, boolean vertical) {
        if (cur == null) {
            return false;
        }
        if (cur.point.equals(p)) {
            return true;
        }
        int cmp;
        if (vertical) {
            cmp = Point2D.X_ORDER.compare(p, cur.point);
        } else {
            cmp = Point2D.Y_ORDER.compare(p, cur.point);
        }
        if (cmp < 0) {
            return search(cur.left, p, !vertical);
        } else {
            return search(cur.right, p, !vertical);
        }
    }

    // draw all points to standard draw
    public void draw() {
        draw(rootNode, true);
    }

    private void draw(Node cur, boolean vertical) {
        if (cur == null) {
            throw new java.lang.NullPointerException();
        }
        if (vertical) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.setPenRadius(0.003);
            StdDraw.line(cur.point.x(), cur.rect.ymin(), cur.point.x(), cur.rect.ymax());
        } else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.setPenRadius(0.003);
            StdDraw.line(cur.rect.xmin(), cur.point.y(), cur.rect.xmax(), cur.point.y());
        }
        if (cur.left != null) {
            draw(cur.left, !vertical);
        }
        if (cur.right != null) {
            draw(cur.right, !vertical);
        }
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        cur.point.draw();
    }

    // all points that are inside the rectangle
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new NullPointerException();
        }
        SET<Point2D> set = new SET<>();
        range(rootNode, rect, set);
        return set;
    }

    private void range(Node cur, RectHV rect, SET<Point2D> set) {
        if (cur == null) {
            return;
        }
        if (!rect.intersects(cur.rect)) {
            return;
        }
        if (rect.contains(cur.point)) {
            set.add(cur.point);
        }
        range(cur.left, rect, set);
        range(cur.right, rect, set);
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        checkIfArgIsNull(p);
        if (isEmpty()) {
            return null;
        }
        return nearest(rootNode, p, rootNode.point, true);
    }

    private Point2D nearest(Node cur, Point2D p, Point2D pNearest, boolean vertical) {
        if (cur == null) {
            return pNearest;
        }
        if (cur.point.equals(p)) {
            return cur.point;
        }
        if (cur.point.distanceTo(p) < pNearest.distanceTo(p)) {
            pNearest = cur.point;
        }
        int cmp;
        if (vertical) {
            cmp = Point2D.X_ORDER.compare(p, cur.point);
        } else {
            cmp = Point2D.Y_ORDER.compare(p, cur.point);
        }
        if (cmp < 0) {
            pNearest = nearest(cur.left, p, pNearest, !vertical);
            if (cur.right != null && pNearest.distanceSquaredTo(p) > cur.right.rect.distanceSquaredTo(p)) {
                pNearest = nearest(cur.right, p, pNearest, !vertical);
            }
        } else {
            pNearest = nearest(cur.right, p, pNearest, !vertical);
            if (cur.left != null && pNearest.distanceSquaredTo(p) > cur.left.rect.distanceSquaredTo(p)) {
                pNearest = nearest(cur.left, p, pNearest, !vertical);
            }
        }
        return pNearest;
    }

    private void checkIfArgIsNull(Point2D p) {
        if (p == null) {
            throw new NullPointerException("Cannot input NUll argument.");
        }
    }

}
