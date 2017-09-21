package jpssena.algorithm.util;

import jpssena.util.Debug;
import org.uma.jmetal.solution.Solution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by João Paulo on 26/08/2017.
 */
public class SolutionSpacing {

    private static class SolutionXComparator <S extends Solution<?>> implements Comparator<S> {
        @Override
        public int compare(S o1, S o2) {
            double xi = o1.getObjective(1) * -1;
            double xj = o2.getObjective(1) * -1;

            return Double.compare(xi, xj);
        }
    }

    /**
     * Call this function to calculate the ideal space between the non dominated solutions.
     * This is one of the first steps in the NSGA-DO Algorithm
     * @param nonDominated List of non dominated solutions. Pareto front 0;
     */
    public static <S extends Solution<?>> double findBestSpacing(List<S> nonDominated) {
        Debug.setCurrentPrintLevel(2);
        Debug.println("Before:");
        for (Solution s : nonDominated) {
            Debug.println("> " + (s.getObjective(1) * -1) + " " + (s.getObjective(0) * -1));
        }

        //At first we need to sort the solutions by one of it's objectives, in the thesis he says to use the "X" value
        //Meaning reduction, because i believe that the accuracy is something derived from the reduction
        Collections.sort(nonDominated, new SolutionXComparator<>());


        Debug.println("After:");
        for (Solution s : nonDominated) {
            Debug.println("> " + (s.getObjective(1) * -1) + " " + (s.getObjective(0) * -1));
        }

        //Then we remove duplicated solutions because they are not going to be useful
        nonDominated = removeDuplicatedSolutions(nonDominated);
        //If we do not have enough solutions, print an error then return
        if (nonDominated.size() < 2) {
            Debug.println("Few Non Dominated Solution in this interaction: " + nonDominated.size());
            return 0;
        }

        //Accumulator of the Sum
        double accumulator = 0;

        //The number of solutions in this generation.
        int numOfSolutions = nonDominated.size();

        //Now we are going to generate K (nS - 1) line equations.
        //We are going to get 2 solutions [si and sj] and find a line that connects these two solutions
        for (int i = 0; i < numOfSolutions - 1; i++) {
            //Gets Si
            Solution si = nonDominated.get(i);
            //Gets Sj
            Solution sj = nonDominated.get(i + 1);

            //Extract the values of the objectives as coordinates in the cartesian plane
            //[Reduction = X][Accuracy == Y]
            double xi = si.getObjective(1)*-1;
            double yi = si.getObjective(0)*-1;

            double xj = sj.getObjective(1)*-1;
            double yj = sj.getObjective(0)*-1;

            //Equation of a line: Y = AkX + Bk. Where Ak is the angular coefficient in the k-th interaction
            //and Bk is the Y intercept of the equation.

            //Ak is calculated by: (yj - yi)/(xj - xi)
            double Ak = (yj - yi)/(xj - xi);

            //As the Geometry Books says
            //To complete the line equation you must choose one of the 2 points and then create the equation.
            //The equation is: Y - Y0 = Ak * (X - X0)
            //If we simplify it, we get: Y = Ak*X - Ak*X0 - Y0
            //Here we are interested in the Bk, so we interested in the result of [-Ak*X0 + Y0], which is going to be a number
            //I opted for choosing the first point
            double Bk = ((Ak * xi * -1) + yi);

            GxFunction GkFunction = new GxFunction(Ak);

            //The Gx function is needed later
            //The Lk is the distance between these to solutions, this could have been found using the formula:
            //Sqrt[(x2 - x1)^2 + (y2 - y1)^2)]
            //But in the thesis he wants us to use the Gk function
            double Lk = GkFunction.result(xj) - GkFunction.result(xi);
            //There's no such thing as a negative distance
            Lk = Math.abs(Lk);

            Debug.println("Si> X: " + xi + " <> Y: " + yi);
            Debug.println("Sj> X: " + xj + " <> Y: " + yj);
            Debug.println("Ak: " + Ak);
            Debug.println("Bk: " + Bk);
            Debug.println("Lk: " + Lk);
            Debug.println("GkFunction(" + xj + "): " + GkFunction.result(xj));
            Debug.println("GkFunction(" + xi + "): " + GkFunction.result(xi));

            //Then, we add the distance into the accumulator
            accumulator += Lk;
        }

        //The ideal spacing is the sum of all Lk divided by the number of solutions;
        double E = accumulator/(numOfSolutions - 1);
        Debug.println("Ideal Spacing: " + E);
        return E;
    }


    /**
     * Finds the ideal points in the pareto front that are equally spaced.
     * @param bestSpacing the best spacing that can be calculated using the findBestSpacing function
     * @param nonDominated The list of non dominated solutions
     * @return a list of ideal points
     */
    public static <S extends Solution<?>> List<Point> findIdealPoints(double bestSpacing, List<S> nonDominated) {
        //Once again we sort the solution list using the X axis
        Collections.sort(nonDominated, new SolutionXComparator<>());

        //Create a list to store the ideal points that are going to be found
        List<Point> idealPoints = new ArrayList<>();

        //if the list has less than 2 solutions, add the solutions as ideal points and then return
        if (nonDominated.size() < 2) {
            for (S solution : nonDominated) {
                idealPoints.add(makePoint(solution));
            }
            return idealPoints;
        }

        //At start add the first solution as an ideal point
        idealPoints.add(makePoint(nonDominated.get(0)));

        //Creates a variable to know when we should create a ideal point, basically this is going to be how long we
        //walked in the pareto front
        double lengthCovered = 0;

        //If we have more than 2 solutions
        if (nonDominated.size() > 2) {
            //For each solution, we want to take solutions as pairs
            for (int k = 0; k < nonDominated.size() - 1; k++) {
                //Gets the pair of solutions
                S si = nonDominated.get(k);
                S sj = nonDominated.get(k + 1);

                //Because solutions are like points in space, we can create a line that unites these solutions
                //Our main point here is to check that if between these solutions there is a ideal point.
                //To do that, we need to first check the distance between these points


                double xi = si.getObjective(1) * -1;
                double yi = si.getObjective(0) * -1;

                double xj = sj.getObjective(1) * -1;
                double yj = sj.getObjective(0) * -1;

                double Vx = xj - xi;
                double Vy = yj - yi;

                double value = Math.sqrt(Math.pow(Vx, 2) + Math.pow(Vy, 2));

                double Ux = Vx / value;
                double Uy = Vy / value;

                double distance = calculateDistanceBetweenSolutions(si, sj);
                double actualPoint = 0;

                while ((lengthCovered + distance) > bestSpacing) {
                    double desiredDistance = bestSpacing - lengthCovered;
                    actualPoint += desiredDistance;

                    double idealX = xi + actualPoint * Ux;
                    double idealY = yi + actualPoint * Uy;

                    Point idealPoint = new Point(idealX, idealY);
                    idealPoints.add(idealPoint);

                    lengthCovered = 0;

                    distance -= desiredDistance;
                }

                lengthCovered += distance;
            }
        }
        Point last = makePoint(nonDominated.get(nonDominated.size() - 1));
        if (!idealPoints.contains(last))
            idealPoints.add(last);

        Debug.println("Points: " + idealPoints);
        return idealPoints;
    }

    public static <S extends Solution<?>> List<S> calculateDistanceToPointsOrdered(List<S> front, List<Point> idealPoints) {
        List<SolutionDistanceToIdeal<S>> solutionsDistance = new ArrayList<>();

        for (S solution : front) {
            double solDistance = Double.MAX_VALUE;

            Point point = makePoint(solution);

            for (Point ideal : idealPoints) {
                double dist = calculateDistanceBetweenPoints(point, ideal);

                if (solDistance > dist) {
                    solDistance = dist;
                }
            }
            SolutionDistanceToIdeal<S> sol = new SolutionDistanceToIdeal<>(solution, solDistance);
            solutionsDistance.add(sol);
            solution.setAttribute("IdealDistance", solDistance);
        }

        Collections.sort(solutionsDistance);
        System.out.println("Distances: " + solutionsDistance);

        List<S> orderedSolutions = new ArrayList<>();
        for (SolutionDistanceToIdeal<S> sol : solutionsDistance) {
            orderedSolutions.add(sol.solution);
        }
        return orderedSolutions;
    }

    private static <S extends Solution<?>> Point makePoint(S solution) {
        double x = solution.getObjective(1) * -1;
        double y = solution.getObjective(0) * -1;

        return new Point(x, y);
    }

    private static double calculateDistanceBetweenSolutions(Solution si, Solution sj) {
        double xi = si.getObjective(1)*-1;
        double yi = si.getObjective(0)*-1;

        double xj = sj.getObjective(1)*-1;
        double yj = sj.getObjective(0)*-1;

        double dx = Math.pow(xj - xi, 2);
        double dy = Math.pow(yj - yi, 2);

        return Math.sqrt(dx + dy);
    }

    private static double calculateDistanceBetweenPoints(Point a, Point b) {
        double xi = a.getX();
        double yi = a.getY();

        double xj = b.getX();
        double yj = b.getY();

        double dx = Math.pow(xj - xi, 2);
        double dy = Math.pow(yj - yi, 2);

        return Math.sqrt(dx + dy);
    }

    private static <S extends Solution<?>> List<S> removeDuplicatedSolutions(List<S> solutions) {
        List<S> unique = new ArrayList<>();
        unique.add(solutions.get(0));
        for (int i = 0; i < solutions.size() - 1; i++) {
            //Gets Si
            S si = solutions.get(i);
            //Gets Sj
            S sj = solutions.get(i + 1);

            //Extract the values of the objectives as coordinates in the cartesian plane
            //[Reduction = X][Accuracy == Y]
            double xi = si.getObjective(1) * -1;
            double xj = sj.getObjective(1) * -1;

            if (xi != xj) {
                unique.add(sj);
            }
        }

        return unique;
    }

    private static class SolutionDistanceToIdeal <S extends Solution<?>> implements Comparable<SolutionDistanceToIdeal<S>>{
        S solution;
        double distance;

        SolutionDistanceToIdeal(S solution, double distance) {
            this.solution = solution;
            this.distance = distance;
        }

        @Override
        public int compareTo(SolutionDistanceToIdeal<S> o) {
            return Double.compare(distance, o.distance);
        }

        @Override
        public String toString() {
            return "> " + distance;
        }
    }
}
