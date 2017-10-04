package jpssena.algorithm.util.solutionattribute;

import jpssena.algorithm.util.Point;
import jpssena.algorithm.util.PointUtil;
import jpssena.util.Debug;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by João Paulo on 20/09/2017.
 */
public class IdealDistance<S extends Solution<?>> extends GenericSolutionAttribute<S, Double> {

    public void computeIdealDistance(List<S> solutionList) {

    }

    public void findBestSpacing(List<S> nonDominated) {
        int numOfSolutions = nonDominated.size();
        //Transforms the list of solutions in a list of points in space based on objectives values
        List<Point> points = PointUtil.getPointsFromSolutionList(nonDominated, 1);

        //Sorts the list based on the objective 1 (Reduction)
        Collections.sort(points);

        double accumulator = 0;
        for (int i = 1; i < points.size(); i++) {
            Point a = points.get(i - 1);
            Point b = points.get(i);

            accumulator += PointUtil.distance(a, b);
        }

        double E = accumulator/(numOfSolutions - 1);
        Debug.println("Ideal Distance is: " + E);


        List<Point> idealPoints = new ArrayList<>();

        if (nonDominated.size() <= 2) {
            idealPoints.addAll(points);
        } else {
            idealPoints.add(points.get(0));

            double lengthCovered = 0;

            for (int k = 0; k < nonDominated.size() - 1; k++) {
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


    }
}
