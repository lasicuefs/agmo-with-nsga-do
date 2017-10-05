package jpssena.operator.selection;

import jpssena.algorithm.util.Point;
import jpssena.algorithm.util.PointUtil;
import jpssena.algorithm.util.Point_Old;
import jpssena.algorithm.util.SolutionSpacing;
import jpssena.algorithm.util.solutionattribute.IdealDistance;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.selection.RankingAndCrowdingSelection;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.DominanceRanking;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by João Paulo on 30/08/2017.
 */
public class RankingAndDistanceOrientedSelection<S extends Solution<?>> implements SelectionOperator<List<S>,List<S>> {
    private int solutionsToSelect;
    private List<S> nonDominated;
    private List<S> solutions;

    //Default constructor
    public RankingAndDistanceOrientedSelection(int maxPopulationSize) {
        solutionsToSelect = maxPopulationSize;
    }

    /**
     * This method is going to do as following:
     * It will try to insert the hole Pareto front into the returned solution list.
     *
     * When the hole front can not be inserted, because it would overflow the amount of solutions it can select,
     * the code will calculate the ideal points in the Pareto front and select the points of the current front that
     * are closer to a ideal point
     *
     * @param solutions the joint population, parent + offspring
     * @return a list of selected solutions to be the new population
     * @throws JMetalException When the list of solution is null, empty or the size of the list is lower than the amount of solutions it needs to select
     */
    public List<S> execute(List<S> solutions) throws JMetalException{
        if (solutions == null)
            throw new JMetalException("Solution list is null");
        else if (solutions.isEmpty())
            throw new JMetalException("The solution list is empty");
        else if (solutions.size() < solutionsToSelect)
            throw new JMetalException("The population size (" + solutions.size() + ") is smaller than " +
                    "the solutions to selected (" + solutionsToSelect + ")");


        //At first we need to calculate the Dominance ranking of the solutions and determine the pareto fronts [0, 1, 2, ...]
        Ranking<S> ranking = new DominanceRanking<>();
        //We call computeRanking(solutions) to do so. And the framework will take care of this part for us
        ranking.computeRanking(solutions);

        //Then we create a reference to the non-dominated solutions front
        //This front will be necessary because the ideal points are calculated based on it
        nonDominated = ranking.getSubfront(0);
        //And we also save the reference to the solution list;
        this.solutions = solutions;

        //We can now call the method that is going to do the selection
        return distanceOrientedSelection(ranking);
    }

    /**
     * The core of the NSGA-DO is done here.
     * Selects based on ranking and then based on the ideal points
     * @param ranking the solutions ranked by dominance
     * @return a list of solutions that will be the new population
     */
    private List<S> distanceOrientedSelection(Ranking<S> ranking) {
        //At first we create a list to store the solutions that are going to be selected.
        //This list will also be the return of this method
        List<S> population = new ArrayList<>(solutionsToSelect);
        //We start at the pareto subfront 0, meaning rankingIndex = 0
        int rankingIndex = 0;

        //While we do not fill the amount of solutions we want to select
        while (population.size() < solutionsToSelect) {
            //We check if the pareto subfront can be entirely copied into the selected list
            if (subfrontFillsIntoThePopulation(ranking, rankingIndex, population)) {
                //If it can, we copy the hole subfront and insert it at the selected list
                addRankedSolutionsToPopulation(ranking, rankingIndex, population);
                //And move forward to the next subfront to repeat the process
                rankingIndex++;
            } else {
                //If it doesn't fit all the solutions in the subfront, we will select those solutions that ate closer to
                //an ideal point. The ideal points are points that are evenly distributed along the pareto front.
                //In the NSGA-DO, this is the measure to try to get diversity.

                //The first step is to calculate what would be the ideal space between these ideal points.
                //We can do that by calling this method. The variable E (called like this in the Doctorate thesis) is the ideal space
                //double E = SolutionSpacing.findBestSpacing(nonDominated);

                //Using this ideal space and the non dominated solutions, we can find the ideal points in to pareto front
                //List<Point_Old> idealPoints = SolutionSpacing.findIdealPoints(E, nonDominated);

                //And by using these ideal points we select only the solutions that are close to them
                computeAndAddDistanceToFront(ranking, rankingIndex, population/*, idealPoints*/);
            }
        }

        return population;
    }

    private void computeAndAddDistanceToFront(Ranking<S> ranking, int rank, List<S> population/*, List<Point_Old> idealPoints*/) {
        //Use the rank parameter to know what list of solutions to fetch
        List<S> front = ranking.getSubfront(rank);

        //Reorder the list using how close each solution is from a ideal point
        //List<S> solutionsOrdered = SolutionSpacing.calculateDistanceToPointsOrdered(front, idealPoints);

        //Creates the Ideal Distance Calculator
        IdealDistance<S> idealDistance = new IdealDistance<>();
        //Computes the distance from every solution to the ideal point
        idealDistance.computeIdealDistance(solutions);
        //Sort the desired front list based on distance to ideal point
        List<S> solutionsOrderedV2 = idealDistance.getOrderedPoints(front);

        int i = 0;
        //Insert the closest points until we fill the selection list
        while (population.size() < solutionsToSelect) {
            population.add(solutionsOrderedV2.get(i)) ;
            i++;
        }

        //This is a trick that is going to be used before refactoring, since the comparator needs a "attribute" 'IdealDistance'
        //we call this to set this attribute in all of the solutions.
        //ATTENTION this line is not a part of the NSGA-DO default behavior
        //TODO Refactor this HackFix so this part stops being a spaghetti code
        //SolutionSpacing.calculateDistanceToPointsOrdered(solutions, idealPoints);
    }

    /**
     * This method were copied from {@link RankingAndCrowdingSelection#addRankedSolutionsToPopulation(Ranking, int, List)} because it is a part of the Ranking selection
     * and it is useful for this part
     */
    private void addRankedSolutionsToPopulation(Ranking<S> ranking, int rank, List<S> population) {
        List<S> front = ranking.getSubfront(rank);
        population.addAll(front);
    }

    /**
     * This method were copied from {@link RankingAndCrowdingSelection#subfrontFillsIntoThePopulation(Ranking, int, List)} because it is a part of the Ranking selection
     * and it is useful for this part
     */
    private boolean subfrontFillsIntoThePopulation(Ranking<S> ranking, int rank, List<S> population) {
        return ranking.getSubfront(rank).size() < (solutionsToSelect - population.size());
    }
}
