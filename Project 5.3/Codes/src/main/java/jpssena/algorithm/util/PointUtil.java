package jpssena.algorithm.util;

import org.uma.jmetal.solution.Solution;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by João Paulo on 04/10/2017.
 */
public class PointUtil {
    public static <S extends Solution<?>> List<Point> getPointsFromSolutionList(List<S> solutions, int sortingIndex) {
        List<Point> points = new ArrayList<>(solutions.size());

        for (S solution : solutions) {
            points.add(new Point(solution, sortingIndex));
        }

        return points;
    }

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

    public static Point difference(Point one, Point other) {
        if (other.getValues().size() != one.getValues().size())
            throw new UnsupportedOperationException("Both points must have the same amount of dimensions");

        List<Double> difference = new ArrayList<>(one.getValues().size());
        for (int i = 0; i < one.getValues().size(); i++) {
            double diff = one.getValues().get(i) - other.getValues().get(i);
            difference.add(diff);
        }

        return new Point(difference, one.getSortingIndex());
    }

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

    public static Point divideByScalar(Point point, double scalar) {
        List<Double> values = new ArrayList<>(point.getValues().size());

        for (Double value : point.getValues()) {
            double divided = value/scalar;
            values.add(divided);
        }

        return new Point(values, point.getSortingIndex());
    }

    public static Point multiplyByScalar(Point point, double scalar) {
        List<Double> values = new ArrayList<>(point.getValues().size());

        for (Double value : point.getValues()) {
            double multiplied = value*scalar;
            values.add(multiplied);
        }

        return new Point(values, point.getSortingIndex());
    }

    public static Point findPointAtDistanceAlongLine(Point a, Point b, double desiredDistance) {
        Point difference = difference(a, b);
        double distance = distance(a, b);
        Point normalized = divideByScalar(difference, distance);
        Point multiplied = multiplyByScalar(normalized, desiredDistance);

        return sum(a, multiplied);
    }
}
