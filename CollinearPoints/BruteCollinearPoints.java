import java.util.ArrayList;
import java.util.Arrays;

public class BruteCollinearPoints {

    private ArrayList<LineSegment> lineSegments = new ArrayList<>();

    // finds all line segments containing 4 points
    public BruteCollinearPoints(Point[] points) {
        if (points == null) {
            throw new NullPointerException();
        }
        
        Point[] pCopy = points.clone();
        Arrays.sort(pCopy);
        
        for (int i = 0; i < pCopy.length - 1; i++) {
            if (pCopy[i].compareTo(pCopy[i + 1]) == 0) {
                throw new IllegalArgumentException();
            }
        }
        
        for (int p = 0; p < pCopy.length - 3; p++) {
            for (int q = p + 1; q < pCopy.length - 2; q++) {
                for (int r = q + 1; r < pCopy.length - 1; r++) {
                    if (Double.compare(pCopy[p].slopeTo(pCopy[q]), pCopy[p].slopeTo(pCopy[r])) == 0) {
                        for (int s = r + 1; s < pCopy.length; s++) {
                            if (Double.compare(pCopy[p].slopeTo(pCopy[q]), pCopy[p].slopeTo(pCopy[s])) == 0) {
                                lineSegments.add(new LineSegment(pCopy[p], pCopy[s]));
                            }
                        }
                    }
                }
            }
        }
    }

    // the number of line segments
    public int numberOfSegments() {
        return lineSegments.size();
    }

    // the line segments
    public LineSegment[] segments() {
        return lineSegments.toArray(new LineSegment[lineSegments.size()]);
    }

}