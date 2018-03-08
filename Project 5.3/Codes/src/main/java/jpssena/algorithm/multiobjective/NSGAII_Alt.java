package jpssena.algorithm.multiobjective;

import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.selection.RankingAndCrowdingSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class NSGAII_Alt<S extends Solution<?>> extends AbstractGeneticAlgorithm<S, List<S>> {
  protected final int maxEvaluations;

  protected final SolutionListEvaluator<S> evaluator;

  protected int evaluations;
  BufferedWriter writer;
    private int iterations = 0;

    protected double iterationFit = -1;
    protected int iterationBal = 0;

  /**
   * Constructor
   */
  public NSGAII_Alt(Problem<S> problem, int maxEvaluations, int populationSize,
      CrossoverOperator<S> crossoverOperator, MutationOperator<S> mutationOperator,
      SelectionOperator<List<S>, S> selectionOperator, SolutionListEvaluator<S> evaluator) {
    super(problem);
    this.maxEvaluations = maxEvaluations;
    setMaxPopulationSize(populationSize); ;

    this.crossoverOperator = crossoverOperator;
    this.mutationOperator = mutationOperator;
    this.selectionOperator = selectionOperator;

    this.evaluator = evaluator;
    try {
      File f = new File("run_nsga_ii.txt");
      f.createNewFile();
        writer = new BufferedWriter(new FileWriter(f, true));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override protected void initProgress() {
    evaluations = getMaxPopulationSize();
    iterations = 1;
  }

  @Override protected void updateProgress() {
    evaluations += getMaxPopulationSize() ;
    iterations++;
  }

  @Override protected boolean isStoppingConditionReached() {
      List<S> population = getPopulation();
      double lvalue = -1;
      for (S individual : population) {
          double acc = individual.getObjective(0);
          double red = individual.getObjective(1);
          double value = Math.sqrt(acc * acc + red * red);
          //double value = acc * red;
          if (value > lvalue) {
              lvalue = value;
          }
      }
      if (lvalue > iterationFit) {
          iterationFit = lvalue;
          iterationBal = 0;
      } else {
          iterationBal++;
      }

      if (iterationBal == 6) {
          try {
              writer.write(getProblem().getName() + ": " + iterations + "\n");
              System.out.println("\n" + getName() + ":" + getProblem().getName() + ":" + iterations + " -> fit: " + iterationFit);
              writer.flush();
          } catch (IOException e) {
              e.printStackTrace();
          }
          return true;
      }

      if (iterations >= 50) {
          try {
              writer.write("capped:" + getName() + ":" + getProblem().getName() + ": " + iterations + "\n");
              System.out.println("\nIterations capped at 50: " + getProblem().getName() + ":" + iterations);
          } catch (IOException e) {
              e.printStackTrace();
          }

          return true;
      }
      return false;
      //return evaluations >= maxEvaluations;
  }

  @Override protected List<S> evaluatePopulation(List<S> population) {
    population = evaluator.evaluate(population, getProblem());

    return population;
  }

  @Override protected List<S> replacement(List<S> population, List<S> offspringPopulation) {
    List<S> jointPopulation = new ArrayList<>();
    jointPopulation.addAll(population);
    jointPopulation.addAll(offspringPopulation);

    RankingAndCrowdingSelection<S> rankingAndCrowdingSelection ;
    rankingAndCrowdingSelection = new RankingAndCrowdingSelection<S>(getMaxPopulationSize()) ;

    return rankingAndCrowdingSelection.execute(jointPopulation) ;
  }

  @Override public List<S> getResult() {
    return getNonDominatedSolutions(getPopulation());
  }

  protected List<S> getNonDominatedSolutions(List<S> solutionList) {
    return SolutionListUtils.getNondominatedSolutions(solutionList);
  }

  @Override public String getName() {
    return "NSGAII" ;
  }

  @Override public String getDescription() {
    return "Nondominated Sorting Genetic Algorithm version II" ;
  }
}
