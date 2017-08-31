package jpssena.operator.selection;

import jpssena.algorithm.util.SolutionSpacing;
import org.uma.jmetal.operator.SelectionOperator;
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

    public RankingAndDistanceOrientedSelection(int maxPopulationSize) {
        solutionsToSelect = maxPopulationSize;
    }

    public int getNumberOfSolutionsToSelect() {
        return solutionsToSelect;
    }

    public List<S> execute(List<S> solutions) throws JMetalException{
        if (solutions == null)
            throw new JMetalException("Solution list is null");
        else if (solutions.isEmpty())
            throw new JMetalException("The solution list is empty");
        else if (solutions.size() < solutionsToSelect)
            throw new JMetalException("The population size ("+solutions.size()+") is smaller than " +
                    "the solutions to selected ("+solutionsToSelect+")");

        Ranking<S> ranking = new DominanceRanking<>();
        ranking.computeRanking(solutions);
        nonDominated = ranking.getSubfront(0);

        return distanceOrientedSelection(ranking);
    }

    private List<S> distanceOrientedSelection(Ranking<S> ranking) {
        List<S> population = new ArrayList<>(solutionsToSelect);
        int rankingIndex = 0;

        SolutionSpacing.findBestSpacing(nonDominated);

        while (population.size() < solutionsToSelect) {
            if (subfrontFillsIntoThePopulation(ranking, rankingIndex, population)) {
                addRankedSolutionsToPopulation(ranking, rankingIndex, population);
                rankingIndex++;
            } else {
                //TODO Do the magic Trick
                System.out.println("Magic needed - Selection Operator");
                break;
            }
        }

        return population;
    }

    private void addRankedSolutionsToPopulation(Ranking<S> ranking, int rank, List<S> population) {
        List<S> front = ranking.getSubfront(rank);
        population.addAll(front);
    }

    private boolean subfrontFillsIntoThePopulation(Ranking<S> ranking, int rank, List<S> population) {
        return ranking.getSubfront(rank).size() < (solutionsToSelect - population.size());
    }
}
