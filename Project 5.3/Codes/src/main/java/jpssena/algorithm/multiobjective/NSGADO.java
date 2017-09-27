package jpssena.algorithm.multiobjective;

import jpssena.algorithm.util.comparator.RankingAndDistanceOrientedComparator;
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
 * This class is the NSGA-DO Java Implementation
 * Most of the code in this class is a copy of the NSGA-II code given that the NSGA-DO is a modification of the NSGA-II.
 * The changed part is commented in the code
 */
public class NSGADO<S extends Solution<?>> extends AbstractGeneticAlgorithm<S, List<S>> {
    private final int maxEvaluations;
    private final SolutionListEvaluator<S> evaluator;
    private int evaluations;

    //Default Constructor
    public NSGADO(Problem<S> problem, int maxEvaluations, int populationSize,
                  CrossoverOperator<S> crossoverOperator, MutationOperator<S> mutationOperator,
                  SelectionOperator<List<S>, S> selectionOperator, SolutionListEvaluator<S> evaluator) {
        super(problem);
        this.maxEvaluations = maxEvaluations;
        setMaxPopulationSize(populationSize);

        this.crossoverOperator = crossoverOperator;
        this.mutationOperator = mutationOperator;
        this.selectionOperator = selectionOperator;

        this.evaluator = evaluator;
    }

    //Copied Method
    @Override
    protected void initProgress() {
        evaluations = getMaxPopulationSize();
    }

    //Copied Method
    @Override
    protected void updateProgress() {
        evaluations += getMaxPopulationSize();
    }

    //Copied Method
    @Override
    protected boolean isStoppingConditionReached() {
        return evaluations >= maxEvaluations;
    }

    //Copied Method
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
        //Creates a list that will contains both the parent and the offspring population
        List<S> jointPopulation = new ArrayList<>();
        //Puts the parent into it
        jointPopulation.addAll(population);
        //And then the offspring
        jointPopulation.addAll(offspringPopulation);

        //This new Ranking class will do the NSGA-DO trick
        RankingAndDistanceOrientedSelection<S> rankingAndIdealSelection
                = new RankingAndDistanceOrientedSelection<>(getMaxPopulationSize());

        //Here we tells the framework to execute and select all the solution it should. (recommended to open the execute() method)
        return rankingAndIdealSelection.execute(jointPopulation);
    }

    //Copied Method
    @Override
    protected List<S> selection(List<S> population) {
        List<S> matingPopulation = new ArrayList<>(population.size());
        for (int i = 0; i < getMaxPopulationSize(); i++) {
            S solution = selectionOperator.execute(population);
            matingPopulation.add(solution);
        }

        return matingPopulation;
    }

    //Copied Method
    @Override
    public List<S> getResult() {
        return getNonDominatedSolutions(getPopulation());
    }

    //Copied Method
    private List<S> getNonDominatedSolutions(List<S> solutions) {
        return SolutionListUtils.getNondominatedSolutions(solutions);
    }

    //Name changed.
    @Override
    public String getName() {
        return "NSGA-DO";
    }

    //Description changed
    @Override
    public String getDescription() {
        return "Nondominated Sorting Genetic Algorithm Distance Oriented";
    }

}
