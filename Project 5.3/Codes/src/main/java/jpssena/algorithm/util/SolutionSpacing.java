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
    public static <S extends Solution<?>> List<Point_Old> findIdealPoints(double bestSpacing, List<S> nonDominated) {
        //Once again we sort the solution list using the X axis
        Collections.sort(nonDominated, new SolutionXComparator<>());

        //Create a list to store the ideal points that are going to be found
        List<Point_Old> idealPoints = new ArrayList<>();

        //if the list has less than 2 solutions, add the solutions as ideal points and then return
        if (nonDominated.size() < 2) {
            for (S solution : nonDominated) {
                idealPoints.add(makePoint(solution));
            }
            return idealPoints;
        }

        //At start add the first solution as an ideal point
        idealPoints.add(makePoint(nonDominated.get(0)));

        //Creates a variable to know when we should create a ideal point, basically this is the length covered since we
        //reached an ideal point (because we added the first point as an ideal it starts at 0)
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
                double distance = calculateDistanceBetweenSolutions(si, sj);

                // -- Find points Comments and Example (Logic) --
                //Then, we need to add this distance to that distance we covered so far, and check if those 2 added together
                //makes a distance that is greater then the best spacing we found (and now it a parameter).

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

                // -- How to Find a Point_Old Comments (Algebra) --
                //To avoid unnecessary recalculation inside of a while loop explained in the last section, we calculate
                //everything we need to make a point away from another along the line right here, before the loop

                //To find a point at any distance D away from a point I along the line with J.
                //we use vector algebra to find these guys

                //Basic Explanation using vectors:
                //      I = (x0, y0)          J = (x1, y1)
                //
                // 1 - Let V be the difference between the end point and the start point
                //      V = (x1 - x0, y1 - y0)  --> V = (Xv, Yv)
                //
                // 2 - Let U be the V vector normalized. To normalize a vector you do:
                //      U = V / (||V||),   if you remember || V || is the length of V
                //
                // 2.1 - The Length of V is the Square Root of the sum of every axis squared, so this will be a number N,
                //       in the end of the day, so we got:
                //      U = V / N
                //
                // 2.2 - So U is actually:
                //      U = (Xv/N, Yv/N)
                //
                // 3 - Now, the point P along the line at a distance D from I is:
                //      P = I + D * U

                //After solving this, you are good to go

                //With that explanation, let's get to the code.
                //At first we extract the information about the solution point (x and y)
                //Starting with Si
                double xi = si.getObjective(1) * -1;
                double yi = si.getObjective(0) * -1;

                //Do the same with Sj
                double xj = sj.getObjective(1) * -1;
                double yj = sj.getObjective(0) * -1;

                //Calculate the vector V as explained before
                double Vx = xj - xi;
                double Vy = yj - yi;

                //Finds the length of the vector V
                double length = Math.sqrt(Math.pow(Vx, 2) + Math.pow(Vy, 2));

                //Calculated the vector U
                double Ux = Vx / length;
                double Uy = Vy / length;

                //Creates a variable to know exactly where we want the point marked in case of situation 3
                double actualPoint = 0;

                //While we still need to mark ideal points between these solutions
                while ((lengthCovered + distance) > bestSpacing) {
                    //We calculate the desiredDistance
                    double desiredDistance = bestSpacing - lengthCovered;
                    //Adds it up to the actual point between these solutions
                    actualPoint += desiredDistance;

                    //Uses the Step 3 of the Algebra Equation to find the axis X and Y of the ideal point
                    double idealX = xi + actualPoint * Ux;
                    double idealY = yi + actualPoint * Uy;

                    //Creates the ideal point
                    Point_Old idealPoint = new Point_Old(idealX, idealY);
                    //and then add it to the list
                    idealPoints.add(idealPoint);

                    //We reset the length covered since we marked a new point
                    lengthCovered = 0;

                    //Decrease distance because we just walked a little bit forward by marking this ideal point
                    distance -= desiredDistance;
                } //End Point_Old marking between 2 points

                //Adds what is left of the distance to length covered, in this case the distance left is aways the distance from the last
                //ideal point to the end solution
                lengthCovered += distance;

            } // End Pair-Pair for
        } //End of If condition

        //In some cases the last point is automatically added, but sometimes it is not.
        //To guarantee that it is aways added.
        //So we make the point
        Point_Old last = makePoint(nonDominated.get(nonDominated.size() - 1));
        //Check if it was already added, and if not, add it
        if (!idealPoints.contains(last))
            idealPoints.add(last);

        Debug.println("Points: " + idealPoints);

        //Then we return the ideal points
        return idealPoints;
    }

    /**
     * This will take the solutions to classify and the ideal points, calculate the distance of each solution to the ideal points
     * and then created a list ordered by the closer a solution is to an ideal point
     * @param front the list of solutions to be classified
     * @param idealPoints the ideal points that can be found using the {@link #findIdealPoints(double, List)}
     * @param <S> The solution type
     * @return The input front list ordered by proximity to a ideal point
     */
    public static <S extends Solution<?>> List<S> calculateDistanceToPointsOrdered(List<S> front, List<Point_Old> idealPoints) {
        //Create a list of solution/distance object that is easier to order using Collections.sort()
        List<SolutionDistanceToIdeal<S>> solutionsDistance = new ArrayList<>();

        //And for each solution in the subfront
        for (S solution : front) {
            //We assume at first that the solution is at infinite distance
            double solDistance = Double.MAX_VALUE;

            //We make a point using the solution
            Point_Old point = makePoint(solution);

            //For each ideal point in ideal points
            for (Point_Old ideal : idealPoints) {
                //calculate the distance between the solution point and the ideal point
                double dist = calculateDistanceBetweenPoints(point, ideal);

                //And select the smallest distance
                if (solDistance > dist) {
                    solDistance = dist;
                }
            }
            //Then create a new entry object of Solution/Distance to store the distance of this solution from an ideal point
            SolutionDistanceToIdeal<S> sol = new SolutionDistanceToIdeal<>(solution, solDistance);

            //Add it to the list
            solutionsDistance.add(sol);

            //And set the attribute 'IdealDistance' that is used by the RankingAndDistanceOrientedComparator class to
            //compare if a solution is closer to an ideal point or not.
            //WARNING: THIS IS NOT A PART OF THE NSGA-DO DEFAULT BEHAVIOR, THIS LINE IS NECESSARY SO THE COMPARATOR CAN WORK
            solution.setAttribute("IdealDistance", solDistance);
        }

        //Now that we've found all of the smallest distances, we can sort the list by distance, smallest to greatest
        Collections.sort(solutionsDistance);
        Debug.println("Distances: " + solutionsDistance);

        //And then return the list to be a list of solutions instead of this Solution/Distance entry
        List<S> orderedSolutions = new ArrayList<>();
        for (SolutionDistanceToIdeal<S> sol : solutionsDistance) {
            orderedSolutions.add(sol.solution);
        }

        //Return the list of solutions
        return orderedSolutions;
    }

    /**
     * Make a point in space using the objectives values
     * @param solution the given solution to make the point
     * @param <S> the solution type
     * @return a Point_Old in space
     */
    private static <S extends Solution<?>> Point_Old makePoint(S solution) {
        double x = solution.getObjective(1) * -1;
        double y = solution.getObjective(0) * -1;

        return new Point_Old(x, y);
    }

    /**
     * Calculates the distance between 2 solutions
     * @param si Solution 1
     * @param sj Solution 2
     * @return the distance between the 2 solutions
     */
    private static double calculateDistanceBetweenSolutions(Solution si, Solution sj) {
        double xi = si.getObjective(1)*-1;
        double yi = si.getObjective(0)*-1;

        double xj = sj.getObjective(1)*-1;
        double yj = sj.getObjective(0)*-1;

        double dx = Math.pow(xj - xi, 2);
        double dy = Math.pow(yj - yi, 2);

        return Math.sqrt(dx + dy);
    }

    /**
     * Calculates the distance between 2 points
     * @param a Point_Old a
     * @param b Point_Old b
     * @return the distance between a and b
     */
    private static double calculateDistanceBetweenPoints(Point_Old a, Point_Old b) {
        double xi = a.getX();
        double yi = a.getY();

        double xj = b.getX();
        double yj = b.getY();

        double dx = Math.pow(xj - xi, 2);
        double dy = Math.pow(yj - yi, 2);

        return Math.sqrt(dx + dy);
    }

    /**
     * Removes duplicated items in the list
     * @param solutions list of solutions to analise
     * @param <S> Solution type
     * @return a list with only unique entries
     */
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

    /**
     * Helper class that are used as Solution/Distance Entry
     * @param <S> Solution Type
     */
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
