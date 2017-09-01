package jpssena.algorithm.util;

import org.uma.jmetal.solution.Solution;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by João Paulo on 26/08/2017.
 */
public class SolutionSpacing {

    /**
     * Call this function to calculate the ideal space between the non dominated solutions.
     * This is one of the first steps in the NSGA-DO Algorithm
     * @param nonDominated List of non dominated solutions. Pareto front 0;
     */
    public static <S extends Solution<?>> void findBestSpacing(List<S> nonDominated) {
        //Accumulator of the Sum
        double accumulator = 0;

        //The number of solutions in this generations.
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
            double Lk = GkFunction.result(xj) - GkFunction.result(xi);
            //There's no such thing as a negative distance
            Lk = Math.abs(Lk);
            System.out.println("Si> X: " + xi + " <> Y: " + yi);
            System.out.println("Sj> X: " + xj + " <> Y: " + yj);
            System.out.println("Ak: " + Ak);
            System.out.println("Bk: " + Bk);
            System.out.println("Lk: " + Lk);
            System.out.println("GkFunction(" + xj + "): " + GkFunction.result(xj));
            System.out.println("GkFunction(" + xi + "): " + GkFunction.result(xi));

            accumulator += Lk;
        }

        //The ideal spacing is the sum of all Lk divided by the number of results;
        double E = accumulator/numOfSolutions;
        System.out.println(E);
    }

    /**
     * Finds the ideal points in the pareto front that are equally spaced.
     * @param bestSpacing the best spacing that can be calculated using the findBestSpacing function
     * @param nonDominated The list of non dominated solutions
     * @return a list of ideal points
     */
    public static <S extends Solution<?>> List<Point> findIdealPoints(double bestSpacing, List<S> nonDominated, double[] Ak, double[] Bk) {
        List<Point> idealPoints = new ArrayList<>();

        double lengthCovered = 0;
        double currentFunctionLength = 0;
        int currentLengthChange = 2;
        int currentRealPoint = 2;

        for (int k = 0; k < nonDominated.size(); k++) {
            Solution si = nonDominated.get(currentRealPoint);
            Solution sj = nonDominated.get(currentRealPoint - 1);
            double distance = calculateDistanceBetweenSolutions(si, sj);

            while ((lengthCovered + distance) > bestSpacing && currentLengthChange == nonDominated.size()) {
                double idealX = 0;

                if (lengthCovered != 0) {
                    //idealX =
                } else {

                }
            }
        }


        return idealPoints;
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
}
