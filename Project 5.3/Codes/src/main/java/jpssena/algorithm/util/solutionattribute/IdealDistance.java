package jpssena.algorithm.util.solutionattribute;

import jpssena.algorithm.util.Point;
import jpssena.algorithm.util.PointUtil;
import jpssena.util.Debug;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.DominanceRanking;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by João Paulo on 20/09/2017.
 */
public class IdealDistance<S extends Solution<?>> extends GenericSolutionAttribute<S, Double> {

    public void computeIdealDistance(List<S> solutionList) {
        Ranking<S> ranking = new DominanceRanking<>();
        ranking.computeRanking(solutionList);

        List<Point> idealPoints = findIdealPoints(ranking.getSubfront(0));

        for (S solution : solutionList) {
            double minDistance = Double.MAX_VALUE;

            Point solutionPoint = new Point(solution);

            for (Point ideal : idealPoints) {
                double distance = PointUtil.distance(solutionPoint, ideal);

                if (distance < minDistance)
                    minDistance = distance;
            }

            solution.setAttribute(getAttributeIdentifier(), minDistance);
        }
    }

    public List<S> getOrderedPoints(List<S> front) {
        Collections.sort(front, new Comparator<S>() {
            @Override
            public int compare(S o1, S o2) {
                if (o2.getAttribute(getAttributeIdentifier()) == null)
                    return 1;
                if (o1.getAttribute(getAttributeIdentifier()) == null)
                    return -1;

                Double distance1 = (Double) o1.getAttribute(getAttributeIdentifier());
                Double distance2 = (Double) o2.getAttribute(getAttributeIdentifier());

                return Double.compare(distance1, distance2);
            }
        });

        return front;
    }

    private List<Point> findIdealPoints(List<S> nonDominated) {
        //Transforms the list of solutions in a list of points in space based on objectives values
        List<Point> points = PointUtil.getPointsFromSolutionList(nonDominated, 1);
        points = PointUtil.removeDuplicated(points);

        int numOfSolutions = points.size();

        //Sorts the list based on the objective 1 (Reduction)
        Collections.sort(points);

        double accumulator = 0;
        for (int i = 1; i < points.size(); i++) {
            Point a = points.get(i - 1);
            Point b = points.get(i);

            accumulator += PointUtil.distance(a, b);
        }

        double E = accumulator/(numOfSolutions - 1);

        List<Point> idealPoints = new ArrayList<>();

        if (points.size() <= 2) {
            idealPoints.addAll(points);
        } else {
            idealPoints.add(points.get(0));

            double lengthCovered = 0;

            for (int k = 0; k < points.size() - 1; k++) {
                Point a = points.get(k);
                Point b = points.get(k + 1);

                double distance = PointUtil.distance(a, b);
                double actualPoint = 0;

                while ((lengthCovered + distance) > E) {
                    double desiredDistance = E - lengthCovered;
                    actualPoint += desiredDistance;

                    idealPoints.add(PointUtil.findPointAtDistanceAlongLine(a, b, actualPoint));

                    lengthCovered = 0;
                    distance -= desiredDistance;
                }

                lengthCovered += distance;
            }

            Point last = points.get(points.size() - 1);
            if (!idealPoints.contains(last)) {
                idealPoints.add(last);
            }
        }
        return idealPoints;

    }
}
