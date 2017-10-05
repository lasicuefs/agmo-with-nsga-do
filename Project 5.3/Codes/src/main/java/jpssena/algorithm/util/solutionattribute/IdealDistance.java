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

    /**
     * Computes the distance between the solutions and the ideal points at the pareto subfront.
     * @param solutionList the population in this generation.
     */
    public void computeIdealDistance(List<S> solutionList) {
        //Creates Dominance ranking to compute the non dominated list
        Ranking<S> ranking = new DominanceRanking<>();
        //computes the dominance ranking
        ranking.computeRanking(solutionList);

        //find ideal points using the pareto subfront 0 (non dominated)
        List<Point> idealPoints = findIdealPoints(ranking.getSubfront(0));

        //For each solution in the solution list, calculate find the distance to the closest ideal point
        for (S solution : solutionList) {
            //Auxiliary variable starts at max value
            double minDistance = Double.MAX_VALUE;

            //Creates a point from solution to calculate distance;
            Point solutionPoint = new Point(solution);

            //Find distance to closest ideal point
            for (Point ideal : idealPoints) {
                double distance = PointUtil.distance(solutionPoint, ideal);

                if (distance < minDistance)
                    minDistance = distance;
            }
            //Sets the attribute of distance from ideal point to the distance found;
            solution.setAttribute(getAttributeIdentifier(), minDistance);
        }
    }

    /**
     * Returns the list received ad parameter ordered by the ideal distance. Smallest to Greatest.
     * @param front the list of solutions to be ordered
     * @return The ordered list
     */
    public List<S> getOrderedPoints(List<S> front) {
        //Sorts a list of solutions based on distance from ideal point
        Collections.sort(front, new Comparator<S>() {
            @Override
            public int compare(S o1, S o2) {
                double distance1 = Double.MAX_VALUE;
                double distance2 = Double.MAX_VALUE;

                if (o1.getAttribute(getAttributeIdentifier()) == null && o2.getAttribute(getAttributeIdentifier()) == null)
                    return 0;

                if (o2.getAttribute(getAttributeIdentifier()) != null)
                    distance2 = (double) o2.getAttribute(getAttributeIdentifier());
                if (o1.getAttribute(getAttributeIdentifier()) != null)
                    distance1 = (double) o1.getAttribute(getAttributeIdentifier());

                return Double.compare(distance1, distance2);
            }
        });

        return front;
    }

    /**
     * Finds the ideal points in the pareto front that are equally spaced.
     * @param nonDominated The list of non dominated solutions
     * @return a list of ideal points
     */
    private List<Point> findIdealPoints(List<S> nonDominated) {
        //Transforms the list of solutions in a list of points in space based on objectives values
        List<Point> points = PointUtil.getPointsFromSolutionList(nonDominated, 1);
        //Remove duplicated points
        points = PointUtil.removeDuplicated(points);

        int numOfSolutions = points.size();

        //Sorts the list based on the objective 1 (Reduction)
        Collections.sort(points);

        //Accumulator of the Sum
        double accumulator = 0;

        //We are going to get 2 points and calculate the distance between them, and add it to the accumulator
        //In the end we will have the full length of the pareto subfront
        for (int i = 1; i < points.size(); i++) {
            Point a = points.get(i - 1);
            Point b = points.get(i);

            accumulator += PointUtil.distance(a, b);
        }

        //Finds the ideal spacing between points dividing the full size by number of solutions - 1
        double E = accumulator/(numOfSolutions - 1);

        //Creates a list of ideal points
        List<Point> idealPoints = new ArrayList<>();

        if (points.size() <= 2) {
            //if we have only 1, 2 or 0 non dominated solutions we say that these are the ideal points
            idealPoints.addAll(points);
        } else {
            //Otherwise, we start at the first solution
            idealPoints.add(points.get(0));

            //Creates a variable to know when we should create a ideal point, basically this is the length covered since we
            //reached an ideal point (because we added the first point as an ideal it starts at 0)
            double lengthCovered = 0;

            //We want to take solutions in pairs
            for (int k = 0; k < points.size() - 1; k++) {
                //References the pair of solutions
                Point a = points.get(k);
                Point b = points.get(k + 1);

                //Because solutions are like points in space, we can create a line that unites these solutions
                //Our main point here is to check that if between these solutions there is a ideal point.
                //To do that, we need to first check the distance between these points
                //So, we need to add this distance to that distance we covered so far, and check if those 2 added together
                //makes a distance that is greater then the best spacing we found.
                double distance = PointUtil.distance(a, b);

                // -- Find points Comments and Example (Logic) --
                //SITUATION 1 - For instance, if we are in the first iteration, we covered 0 distance so far, and the distance between
                //the first 2 solutions is 3. But the ideal spacing is 1.8
                //That means that we need a point that is 1.8 away from Si along the line between Si and Sj.

                //SITUATION 2 - In another scenario, the ideal spacing is 3.5, but the length covered were 0 and the distance is 2.
                //In this case, do not have to place a ideal point between those. And then we need to add up the 2 to the
                //distance covered and go to the next iteration. Let's now say that in this iteration the distance is 3;
                //Well, 3 + 2 is definitely greater than 3.5, so we need to place a point between these solutions, but
                //where should it be?
                //Given that we already went thought the previous iteration without creating a point, that means we advanced
                //2 in space, meaning that we need to mark a point in 3.5 - 2 (ideal - covered) = 1.5 away from Si along
                //the line from Si to Sj

                //SITUATION 3 - There could be also moments that the ideal spacing is 1 and the distance is 7. In this cases we will
                //need to mark up to 7 ideal points between these solutions inside of a while loop


                //After this explanation, we are good to go
                //Creates a variable to know exactly where we want the point marked in case of situation 3
                double actualPoint = 0;

                //While we still need to mark ideal points between these points
                while ((lengthCovered + distance) > E) {
                    //We calculate the desiredDistance
                    double desiredDistance = E - lengthCovered;
                    //Adds it to the actual point between these solutions
                    actualPoint += desiredDistance;

                    //Finds the point and add it to the ideal points list
                    idealPoints.add(PointUtil.findPointAtDistanceAlongLine(a, b, actualPoint));

                    //We reset the length covered since we marked a new point
                    lengthCovered = 0;
                    //Decrease distance because we just walked a little bit forward by marking this ideal point
                    distance -= desiredDistance;
                }
                //Adds what is left of the distance to length covered, in this case the distance left is aways the distance from the last
                //ideal point to the end solution
                lengthCovered += distance;
            } // End of Point Pair - Pair for

            //In some cases the last point is automatically added, but sometimes it is not.
            //To guarantee that it is aways added.
            Point last = points.get(points.size() - 1);
            if (!idealPoints.contains(last)) {
                //if it were not added, add it
                idealPoints.add(last);
            }
        }

        //return the ideal points
        return idealPoints;
    }
}
