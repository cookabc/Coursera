import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;

public class FastCollinearPoints {

    private final ArrayList<LineSegment> segments = new ArrayList<>();

    // finds all line segments containing 4 or more points
    public FastCollinearPoints(Point[] points) {
        if (points == null) {
            throw new IllegalArgumentException();
        }
        // if one point is null, NPE will be thrown
        try {
            Arrays.sort(points);
        } catch (NullPointerException npe) {
            throw new IllegalArgumentException();
        }
        // make sure no overlapping points
        for (int i = 1; i < points.length; i++) {
            if (points[i].compareTo(points[i - 1]) == 0) {
                throw new IllegalArgumentException();
            }
        }

        for (int i = 0; i < points.length - 3; i++) {
            Point[] pCopy = points.clone();
            Arrays.sort(pCopy, points[i].slopeOrder());
            for (int p = 0, start = 1, end = 2; end < pCopy.length; end++) {
                while (end < pCopy.length &&
                        Double.compare(pCopy[p].slopeTo(pCopy[start]), pCopy[p].slopeTo(pCopy[end])) == 0) {
                    end++;
                }
                if (end - start >= 3 && pCopy[p].compareTo(pCopy[start]) < 0) {
                    this.segments.add(new LineSegment(pCopy[p], pCopy[end - 1]));
                }
                start = end;
            }
        }
    }

    // the number of line segments
    public int numberOfSegments() {
        return this.segments.size();
    }

    // the line segments
    public LineSegment[] segments() {
        LineSegment[] lineSegments = new LineSegment[this.segments.size()];
        for (int i = 0; i < this.segments.size(); i++) {
            lineSegments[i] = this.segments.get(i);
        }
        return lineSegments;
    }

    public static void main(String[] args) {
        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        StdOut.println(collinear.numberOfSegments());
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
