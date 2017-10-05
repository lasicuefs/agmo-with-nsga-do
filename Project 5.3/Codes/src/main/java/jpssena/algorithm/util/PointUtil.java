package jpssena.algorithm.util;

import org.uma.jmetal.solution.Solution;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by João Paulo on 02/10/2017.
 */
public class PointUtil {

    /**
     * Creates a Point List based on a Solution list
     * @param solutions List of Solutions to be transformed
     * @param sortingIndex Index used to sort the Point list
     * @param <S> Type of Solution
     * @return A point List
     */
    public static <S extends Solution<?>> List<Point> getPointsFromSolutionList(List<S> solutions, int sortingIndex) {
        List<Point> points = new ArrayList<>(solutions.size());

        for (S solution : solutions) {
            points.add(new Point(solution, sortingIndex));
        }

        return points;
    }

    /**
     * Returns the Distance between 2 points in space using euclidean distance
     * @param one The first point
     * @param other The Second Point
     * @return The distance between these 2 points
     */
    public static double distance(Point one, Point other) {
        if (other.getValues().size() != one.getValues().size())
            throw new UnsupportedOperationException("Both points must have the same amount of dimensions");

        double number = 0;

        for (int i = 0; i < one.getValues().size(); i++) {
            Double a = one.getValues().get(i);
            Double b = other.getValues().get(i);

            number += Math.pow(a - b, 2);
        }

        return Math.sqrt(number);
    }

    /**
     * Returns a point that is the difference vector between point A and B;
     * @param one The First point
     * @param other The Second point
     * @return A point that represents the difference between the 2 points given
     */
    public static Point difference(Point one, Point other) {
        if (other.getValues().size() != one.getValues().size())
            throw new UnsupportedOperationException("Both points must have the same amount of dimensions");

        List<Double> differences = new ArrayList<>(one.getValues().size());
        for (int i = 0; i < one.getValues().size(); i++) {
            double diff = one.getValues().get(i) - other.getValues().get(i);
            differences.add(diff);
        }

        return new Point(differences, one.getSortingIndex());
    }

    /**
     * Returns a point that represents the sum vector of point A and B
     * @param one The First point
     * @param other The Second point
     * @return The sum of the points
     */
    public static Point sum(Point one, Point other) {
        if (other.getValues().size() != one.getValues().size())
            throw new UnsupportedOperationException("Both points must have the same amount of dimensions");

        List<Double> sums = new ArrayList<>(one.getValues().size());
        for (int i = 0; i < one.getValues().size(); i++) {
            double sum = one.getValues().get(i) + other.getValues().get(i);
            sums.add(sum);
        }

        return new Point(sums, one.getSortingIndex());
    }

    /**
     * Divides the point indexes by an scalar value.
     * @param point The point to be divided
     * @param scalar The scalar value
     * @return The point divided by the scalar
     */
    public static Point divideByScalar(Point point, double scalar) {
        List<Double> values = new ArrayList<>(point.getValues().size());

        for (Double value : point.getValues()) {
            double divided = value/scalar;
            values.add(divided);
        }

        return new Point(values, point.getSortingIndex());
    }

    /**
     * Multiplies the point indexes by an scalar value.
     * @param point The point to be divided
     * @param scalar The scalar value
     * @return The point Multiplied by the scalar
     */
    public static Point multiplyByScalar(Point point, double scalar) {
        List<Double> values = new ArrayList<>(point.getValues().size());

        for (Double value : point.getValues()) {
            double multiplied = value*scalar;
            values.add(multiplied);
        }

        return new Point(values, point.getSortingIndex());
    }

    /**
     * Finds the point P at a distance d from A along the line between A and B.
     * This method uses Linear Algebra formula (capital letters are vectors, and the others are scalars):
     *
     * D = B - A (Difference vector)
     * V = D / || D || (D normalized); ||D|| is the euclidean distance between A and B
     * P = A + d * V
     *
     * @param a Point A
     * @param b Point B
     * @param desiredDistance Distance from A
     * @return The point found.
     */
    public static Point findPointAtDistanceAlongLine(Point a, Point b, double desiredDistance) {
        Point difference = difference(b, a);
        double distance = distance(a, b);
        Point normalized = divideByScalar(difference, distance);
        Point multiplied = multiplyByScalar(normalized, desiredDistance);

        return sum(a, multiplied);
    }

    /**
     * Removes duplicated points in a list
     * @param points List of points to be analyzed
     * @return A list of unique points
     */
    public static List<Point> removeDuplicated(List<Point> points) {
        List<Point> uniques = new ArrayList<>();

        for (Point point : points) {
            if (!uniques.contains(point))
                uniques.add(point);
        }

        return uniques;
    }
}
