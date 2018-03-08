package jpssena.algorithm.multiobjective;

import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaiii.NSGAIIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.nsgaiii.util.EnvironmentalSelection;
import org.uma.jmetal.algorithm.multiobjective.nsgaiii.util.ReferencePoint;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.DominanceRanking;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by ajnebro on 30/10/14.
 * Modified by Juanjo on 13/11/14
 *
 * This implementation is based on the code of Tsung-Che Chiang
 * http://web.ntnu.edu.tw/~tcchiang/publications/nsga3cpp/nsga3cpp.htm
 */
@SuppressWarnings("serial")
public class NSGAIII_II<S extends Solution<?>> extends AbstractGeneticAlgorithm<S, List<S>> {
  protected int iterations ;
  protected int maxIterations ;
  int evaluated = 0;

  protected double iterationFit = -1;
  protected int iterationBal = 0;

  protected SolutionListEvaluator<S> evaluator ;

  protected Vector<Integer> numberOfDivisions  ;
  protected List<ReferencePoint<S>> referencePoints = new Vector<>() ;

    BufferedWriter writer;

  /** Constructor */
  public NSGAIII_II(NSGAIIIBuilder<S> builder) { // can be created from the NSGAIIIBuilder within the same package
    super(builder.getProblem()) ;
    maxIterations = builder.getMaxIterations() ;

    crossoverOperator =  builder.getCrossoverOperator() ;
    mutationOperator  =  builder.getMutationOperator() ;
    selectionOperator =  builder.getSelectionOperator() ;

    evaluator = builder.getEvaluator() ;

    /// NSGAIII
    numberOfDivisions = new Vector<>(1) ;
    numberOfDivisions.add(6);
    //numberOfDivisions.add(builder.getPopulationSize() - 1);
    //numberOfDivisions.add(12) ; // Default value for 3D problems

    (new ReferencePoint<S>()).generateReferencePoints(referencePoints, getProblem().getNumberOfObjectives() , numberOfDivisions);

    int populationSize = referencePoints.size();
    System.out.println(referencePoints.size());
    while (populationSize%4>0) {
      populationSize++;
    }

    setMaxPopulationSize(builder.getPopulationSize());
    System.out.println("Population Size: " + builder.getPopulationSize());
    JMetalLogger.logger.info("rpssize: " + referencePoints.size());

      try {
          File f = new File("run_nsga_iii.txt");
          f.createNewFile();
          writer = new BufferedWriter(new FileWriter(f, true));
      } catch (IOException e) {
          e.printStackTrace();
      }
  }

  @Override
  protected void initProgress() {
    iterations = 1 ;
  }

  @Override
  protected void updateProgress() {
    iterations++ ;
  //  System.out.println("Iteration " + iterations);
  }

  @Override
  protected boolean isStoppingConditionReached() {
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
    //return iterations >= maxIterations;
  }

  @Override
  protected List<S> evaluatePopulation(List<S> population) {
    population = evaluator.evaluate(population, getProblem()) ;
    evaluated++;
    return population ;
  }

    @Override
    protected List<S> selection(List<S> population) {
        List<S> matingPopulation = new ArrayList<>(population.size()) ;
        for (int i = 0; i < getMaxPopulationSize(); i++) {
          S solution = selectionOperator.execute(population);
          matingPopulation.add(solution) ;
        }

        return matingPopulation;
    }

  @Override
  protected List<S> reproduction(List<S> population) {
    List<S> offspringPopulation = new ArrayList<>(getMaxPopulationSize());
    for (int i = 0; i < getMaxPopulationSize(); i+=2) {
      List<S> parents = new ArrayList<>(2);
      parents.add(population.get(i));
      parents.add(population.get(Math.min(i + 1, getMaxPopulationSize()-1)));

      List<S> offspring = crossoverOperator.execute(parents);

      mutationOperator.execute(offspring.get(0));
      mutationOperator.execute(offspring.get(1));

      offspringPopulation.add(offspring.get(0));
      offspringPopulation.add(offspring.get(1));
    }
    return offspringPopulation ;
  }

  
  private List<ReferencePoint<S>> getReferencePointsCopy() {
	  List<ReferencePoint<S>> copy = new ArrayList<>();
	  for (ReferencePoint<S> r : this.referencePoints) {
		  copy.add(new ReferencePoint<>(r));
	  }
	  return copy;
  }
  
  @Override
  protected List<S> replacement(List<S> population, List<S> offspringPopulation) {
   
	List<S> jointPopulation = new ArrayList<>();
    jointPopulation.addAll(population) ;
    jointPopulation.addAll(offspringPopulation) ;

    Ranking<S> ranking = computeRanking(jointPopulation);
    
    //List<Solution> pop = crowdingDistanceSelection(ranking);
    List<S> pop = new ArrayList<>();
    List<List<S>> fronts = new ArrayList<>();
    int rankingIndex = 0;
    int candidateSolutions = 0;
    while (candidateSolutions < getMaxPopulationSize()) {
      fronts.add(ranking.getSubfront(rankingIndex));
      candidateSolutions += ranking.getSubfront(rankingIndex).size();
      if ((pop.size() + ranking.getSubfront(rankingIndex).size()) <= getMaxPopulationSize())
        addRankedSolutionsToPopulation(ranking, rankingIndex, pop);
      rankingIndex++;
    }
    
    // A copy of the reference list should be used as parameter of the environmental selection
    EnvironmentalSelection<S> selection =
            new EnvironmentalSelection<>(fronts,getMaxPopulationSize(),getReferencePointsCopy(),
                    getProblem().getNumberOfObjectives());
    
    pop = selection.execute(pop);
     
    return pop;
  }

  @Override
  public List<S> getResult() {
    return getNonDominatedSolutions(getPopulation()) ;
  }

  protected Ranking<S> computeRanking(List<S> solutionList) {
    Ranking<S> ranking = new DominanceRanking<>() ;
    ranking.computeRanking(solutionList) ;

    return ranking ;
  }

  protected void addRankedSolutionsToPopulation(Ranking<S> ranking, int rank, List<S> population) {
    List<S> front ;

    front = ranking.getSubfront(rank);

    for (int i = 0 ; i < front.size(); i++) {
      population.add(front.get(i));
    }
  }

  protected List<S> getNonDominatedSolutions(List<S> solutionList) {
    return SolutionListUtils.getNondominatedSolutions(solutionList) ;
  }

  @Override public String getName() {
    return "NSGAIII" ;
  }

  @Override public String getDescription() {
    return "Nondominated Sorting Genetic Algorithm version III" ;
  }

}