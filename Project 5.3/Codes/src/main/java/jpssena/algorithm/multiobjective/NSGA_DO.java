package jpssena.algorithm.multiobjective;

import jpssena.operator.selection.RankingAndDistanceOrientedSelection;
import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by João Paulo on 29/08/2017.
 */
public class NSGA_DO<S extends Solution<?>> extends AbstractGeneticAlgorithm<S, List<S>> {
    private final int maxEvaluations;
    private final SolutionListEvaluator<S> evaluator;
    private int evaluations;

    public NSGA_DO(Problem<S> problem, int maxEvaluations, int populationSize,
                   CrossoverOperator<S> crossoverOperator, MutationOperator<S> mutationOperator,
                   SelectionOperator<List<S>, S> selectionOperator, SolutionListEvaluator<S> evaluator) {
        super(problem);
        this.maxEvaluations = maxEvaluations;
        setMaxPopulationSize(populationSize); ;

        this.crossoverOperator = crossoverOperator;
        this.mutationOperator = mutationOperator;
        this.selectionOperator = selectionOperator;

        this.evaluator = evaluator;
    }

    @Override
    protected void initProgress() {
        evaluations = getMaxPopulationSize();
    }

    @Override
    protected void updateProgress() {
        evaluations += getMaxPopulationSize();
    }

    @Override
    protected boolean isStoppingConditionReached() {
        return evaluations >= maxEvaluations;
    }

    @Override
    protected List<S> evaluatePopulation(List<S> population) {
        population = evaluator.evaluate(population, getProblem());
        return population;
    }

    /**
     * Here it happens differential in the NSGA-DO
     * @param population The father population
     * @param offspringPopulation The brand new generation created
     * @return A list of selected elements from the population
     */
    @Override
    protected List<S> replacement(List<S> population, List<S> offspringPopulation) {
        List<S> jointPopulation = new ArrayList<>();
        jointPopulation.addAll(population);
        jointPopulation.addAll(offspringPopulation);

        //This new Ranking will do the NSGA-DO trick
        RankingAndDistanceOrientedSelection<S> rankingAndIdealSelection
                = new RankingAndDistanceOrientedSelection<S>(getMaxPopulationSize());
        return rankingAndIdealSelection.execute(jointPopulation);
    }

    @Override
    public List<S> getResult() {
        return getNonDominatedSolutions(getPopulation());
    }

    private List<S> getNonDominatedSolutions(List<S> solutions) {
        return SolutionListUtils.getNondominatedSolutions(solutions);
    }

    @Override
    public String getName() {
        return "NSGA-DO";
    }

    @Override
    public String getDescription() {
        return "Nondominated Sorting Genetic Algorithm Distance Oriented";
    }

}
