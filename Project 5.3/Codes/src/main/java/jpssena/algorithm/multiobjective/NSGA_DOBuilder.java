package jpssena.algorithm.multiobjective;

import jpssena.algorithm.util.comparator.RankingAndDistanceOrientedComparator;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.AlgorithmBuilder;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

import java.util.List;

/**
 * Created by João Paulo on 31/08/2017.
 */
public class NSGA_DOBuilder<S extends Solution<?>> implements AlgorithmBuilder<NSGA_DO<S>> {
    private final Problem<S> problem;
    private int maxEvaluations;
    private int populationSize;
    private CrossoverOperator<S> crossoverOperator;
    private MutationOperator<S> mutationOperator;
    private SelectionOperator<List<S>, S> selectionOperator;
    private SolutionListEvaluator<S> evaluator;

    public NSGA_DOBuilder(Problem<S> problem, CrossoverOperator<S> crossoverOperator, MutationOperator<S> mutationOperator) {
        this.problem = problem;
        maxEvaluations = 25000;
        populationSize = 100;
        this.crossoverOperator = crossoverOperator ;
        this.mutationOperator = mutationOperator ;
        selectionOperator = new BinaryTournamentSelection<>(new RankingAndDistanceOrientedComparator<S>()) ;
        evaluator = new SequentialSolutionListEvaluator<>();
    }

    public NSGA_DOBuilder<S> setMaxEvaluations(int maxEvaluations) {
        if (maxEvaluations < 0)
            throw new JMetalException("maxEvaluations is negative: " + maxEvaluations);

        this.maxEvaluations = maxEvaluations;
        return this;
    }

    public NSGA_DOBuilder<S> setPopulationSize(int populationSize) {
        if (populationSize < 0)
            throw new JMetalException("Population size is negative: " + populationSize);

        this.populationSize = populationSize;
        return this;
    }

    public NSGA_DOBuilder<S> setSelectionOperator(SelectionOperator<List<S>, S> selectionOperator) {
        if (selectionOperator == null)
            throw new JMetalException("Selection Operator is null");

        this.selectionOperator = selectionOperator;
        return this;
    }

    public NSGA_DOBuilder<S> setSolutionListEvaluator(SolutionListEvaluator<S> evaluator) {
        if (evaluator == null)
            throw new JMetalException("Evaluator is null");

        this.evaluator = evaluator;
        return this;
    }

    @Override
    public NSGA_DO<S> build() {
        NSGA_DO<S> algorithm = new NSGA_DO<>(problem, maxEvaluations, populationSize, crossoverOperator, mutationOperator, selectionOperator, evaluator);
        return algorithm;
    }

    public Problem<S> getProblem() {
        return problem;
    }

    public int getMaxIterations() {
        return maxEvaluations;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public CrossoverOperator<S> getCrossoverOperator() {
        return crossoverOperator;
    }

    public MutationOperator<S> getMutationOperator() {
        return mutationOperator;
    }

    public SelectionOperator<List<S>, S> getSelectionOperator() {
        return selectionOperator;
    }

    public SolutionListEvaluator<S> getSolutionListEvaluator() {
        return evaluator;
    }
}
