import java.util.ArrayList;
import java.util.Arrays;

public class BruteCollinearPoints {

    private final ArrayList<LineSegment> segments = new ArrayList<>();

    // finds all line segments containing 4 or more points
    public BruteCollinearPoints(Point[] points) {
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
        for (int i = 0; i < points.length; i++) {
            if (i > 0 && points[i].compareTo(points[i - 1]) == 0) {
                throw new IllegalArgumentException();
            }
        }
        for (int p = 0; p < points.length - 3; p++) {
            for (int q = p + 1; q < points.length - 2; q++) {
                for (int r = q + 1; r < points.length - 1; r++) {
                    if (points[p].slopeTo(points[q]) == points[p].slopeTo(points[r])) {
                        for (int s = r + 1; s < points.length; s++) {
                            if (points[p].slopeTo(points[q]) == points[p].slopeTo(points[s])) {
                                this.segments.add(new LineSegment(points[p], points[s]));
                            }
                        }
                    }
                }
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
}
