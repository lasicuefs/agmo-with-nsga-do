package jpssena.algorithm.util;

import org.uma.jmetal.solution.Solution;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by João Paulo on 02/10/2017.
 */
public class Point implements Comparable<Point>{
    private List<Double> values;
    private int sortingIndex;

    public Point(Solution solution) {
        makePoint(solution);
        sortingIndex = 0;
    }

    public Point(int sortingIndex, double... val) {
        this.sortingIndex = sortingIndex;
        makePoint(val);
    }

    public Point(List<Double> values, int sortingIndex) {
        this.values = values.subList(0, values.size());
        this.sortingIndex = sortingIndex;
    }

    public Point(Solution solution, int sortingIndex) {
        makePoint(solution);
        this.sortingIndex = sortingIndex;
    }

    private void makePoint(Solution solution) {
        int objectives = solution.getNumberOfObjectives();
        values = new ArrayList<>(objectives);

        for (int i = 0; i < objectives; i++) {
            values.add(solution.getObjective(i) > 0 ? solution.getObjective(i) : solution.getObjective(i) * -1);
        }
    }

    private void makePoint(double[] val) {
        int objectives = val.length;
        values = new ArrayList<>(objectives);

        for (double aVal : val) {
            values.add(aVal);
        }
    }

    public int getSortingIndex() {
        return sortingIndex;
    }

    public List<Double> getValues() {
        return values;
    }

    @Override
    public int compareTo(Point o) {
        return Double.compare(values.get(sortingIndex), o.getValues().get(sortingIndex));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Point) {
            Point other = (Point)obj;

            return PointUtil.distance(this, other) <= 0.00001;
        }

        return false;
    }

    @Override
    public String toString() {
        return values.toString();
    }
}
