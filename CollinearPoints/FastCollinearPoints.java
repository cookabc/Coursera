import java.util.ArrayList;
import java.util.Arrays;

public class FastCollinearPoints {

    private ArrayList<LineSegment> lineSegments = new ArrayList<>();

    // finds all line segments containing 4 or more points
    public FastCollinearPoints(Point[] points) {
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

        for (int i = 0; i < pCopy.length - 3; i++) {
            Arrays.sort(pCopy);
            Arrays.sort(pCopy, pCopy[i].slopeOrder());

            for (int p = 0, first = 1, last = 2; last < pCopy.length; last++) {
                // find last collinear to p point
                while (last < pCopy.length
                        && Double.compare(pCopy[p].slopeTo(pCopy[first]), pCopy[p].slopeTo(pCopy[last])) == 0) {
                    last++;
                }
                // if found at least 3 elements, make segment if it's unique
                if (last - first >= 3 && pCopy[p].compareTo(pCopy[first]) < 0) {
                    lineSegments.add(new LineSegment(pCopy[p], pCopy[last - 1]));
                }
                // Try to find next
                first = last;
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