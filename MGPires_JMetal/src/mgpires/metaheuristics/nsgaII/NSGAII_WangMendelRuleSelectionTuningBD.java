//  NSGAII_WangMendelRuleSelectionTuningBD.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package mgpires.metaheuristics.nsgaII;

import jmetal.core.*;
import jmetal.encodings.solutionType.ArrayRealAndBinarySolutionType;
import jmetal.encodings.solutionType.BinarySolutionType;
import jmetal.encodings.variable.ArrayReal;
import jmetal.encodings.variable.Binary;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.Distance;
import jmetal.util.JMException;
import mgpires.solutionType.ArrayBinarySolutionType;

/** 
 *  Implementation of NSGA-II.
 *  This implementation of NSGA-II makes use of a QualityIndicator object
 *  to obtained the convergence speed of the algorithm. This version is used
 *  in the paper:
 *     A.J. Nebro, J.J. Durillo, C.A. Coello Coello, F. Luna, E. Alba 
 *     "A Study of Convergence Speed in Multi-Objective Metaheuristics." 
 *     To be presented in: PPSN'08. Dortmund. September 2008.
 */

public class NSGAII_WangMendelRuleSelectionTuningBD extends Algorithm {

/**
 * Constructor
 * @param problem Problem to solve
 */
public NSGAII_WangMendelRuleSelectionTuningBD(Problem problem) {
    super (problem) ;
} // NSGAII_WangMendelRuleSelectionTuningBD

  /**   
   * Runs the NSGA-II algorithm.
   * @return a <code>SolutionSet</code> that is a set of non dominated solutions
   * as a result of the algorithm execution
   * @throws JMException 
   * @throws ClassNotFoundException 
   */
@Override
public SolutionSet execute() throws JMException, ClassNotFoundException {
  
    int populationSize;
    int maxEvaluations;
    int evaluations;

    QualityIndicator indicators; // QualityIndicator object
    int requiredEvaluations; // Use in the example of use of the
    // indicators object (see below)

    SolutionSet population;
    SolutionSet offspringPopulation;
    SolutionSet union;
    
    // testing population2
    Solution population2;

    Operator mutationOperator;
    Operator crossoverOperator;
    Operator selectionOperator;

    Distance distance = new Distance();

    //Read the parameters
    populationSize = ((Integer) getInputParameter("populationSize"));
    //maxEvaluations = ((Integer) getInputParameter("maxEvaluations")).intValue();
    
    // code modified in according to hint of the Netbeans
    //populationSize = ((Integer) getInputParameter("populationSize"));
    //maxEvaluations = ((Integer) getInputParameter("maxEvaluations"));
    //indicators = (QualityIndicator) getInputParameter("indicators");

    //Initialize the variables
    population = new SolutionSet(populationSize);
    evaluations = 0;

    requiredEvaluations = 0;

    //Read the operators
    //mutationOperator = operators_.get("mutation");
    crossoverOperator = operators_.get("crossover");
    //selectionOperator = operators_.get("selection");

    // Create the initial solutionSet
    Solution newSolution;
    
    Solution[] parents = new Solution[2];
    for (int i = 0; i < populationSize; i++) {
      newSolution = new Solution(problem_);
      //problem_.evaluate(newSolution);
      //problem_.evaluateConstraints(newSolution);
      evaluations++;
      population.add(newSolution);
      
      parents[i] = newSolution;
    } //for   
    
    System.out.println("Length of parents............: " + parents.length);
    System.out.println("Number of decision variables.: " + parents[0].getDecisionVariables().length);
    System.out.println("Number of bits p1 and p2.....: " + parents[0].getNumberOfBits() + " " + parents[1].getNumberOfBits());
    
    Solution[] offSpring;
    offSpring = (Solution[]) crossoverOperator.execute(parents);
    printOffSpring(offSpring);
    
   
    
    System.out.println("");
    System.out.println("Printing the population...");  
    printPopulation(population);
    

    /*
    // Generations 
    while (evaluations < maxEvaluations) {

      // Create the offSpring solutionSet      
      offspringPopulation = new SolutionSet(populationSize);
      Solution[] parents = new Solution[2];
      for (int i = 0; i < (populationSize / 2); i++) {
        if (evaluations < maxEvaluations) {
          //obtain parents
          parents[0] = (Solution) selectionOperator.execute(population);
          parents[1] = (Solution) selectionOperator.execute(population);
          Solution[] offSpring = (Solution[]) crossoverOperator.execute(parents);
          mutationOperator.execute(offSpring[0]);
          mutationOperator.execute(offSpring[1]);
          problem_.evaluate(offSpring[0]);
          problem_.evaluateConstraints(offSpring[0]);
          problem_.evaluate(offSpring[1]);
          problem_.evaluateConstraints(offSpring[1]);
          offspringPopulation.add(offSpring[0]);
          offspringPopulation.add(offSpring[1]);
          evaluations += 2;
        } // if                            
      } // for

      // Create the solutionSet union of solutionSet and offSpring
      union = ((SolutionSet) population).union(offspringPopulation);

      // Ranking the union
      Ranking ranking = new Ranking(union);

      int remain = populationSize;
      int index = 0;
      SolutionSet front = null;
      population.clear();

      // Obtain the next front
      front = ranking.getSubfront(index);

      while ((remain > 0) && (remain >= front.size())) {
        //Assign crowding distance to individuals
        distance.crowdingDistanceAssignment(front, problem_.getNumberOfObjectives());
        //Add the individuals of this front
        for (int k = 0; k < front.size(); k++) {
          population.add(front.get(k));
        } // for

        //Decrement remain
        remain = remain - front.size();

        //Obtain the next front
        index++;
        if (remain > 0) {
          front = ranking.getSubfront(index);
        } // if        
      } // while

      // Remain is less than front(index).size, insert only the best one
      if (remain > 0) {  // front contains individuals to insert                        
        distance.crowdingDistanceAssignment(front, problem_.getNumberOfObjectives());
        front.sort(new CrowdingComparator());
        for (int k = 0; k < remain; k++) {
          population.add(front.get(k));
        } // for

        remain = 0;
      } // if                               

      // This piece of code shows how to use the indicator object into the code
      // of NSGA-II. In particular, it finds the number of evaluations required
      // by the algorithm to obtain a Pareto front with a hypervolume higher
      // than the hypervolume of the true Pareto front.
      if ((indicators != null) &&
          (requiredEvaluations == 0)) {
        double HV = indicators.getHypervolume(population);
        if (HV >= (0.98 * indicators.getTrueParetoFrontHypervolume())) {
          requiredEvaluations = evaluations;
        } // if
      } // if
    } // while

    // Return as output parameter the required evaluations
    setOutputParameter("evaluations", requiredEvaluations);

    // Return the first non-dominated front
    Ranking ranking = new Ranking(population);
    ranking.getSubfront(0).printFeasibleFUN("FUN_NSGAII") ;

    return ranking.getSubfront(0);
  */
    return population;
} // execute  

    
    /**
     * printoffSpring print the offspring
     * @param offSpring is one vector with 2 offspring
    */ 
    public void printOffSpring(Solution[] offSpring) {     
        
        Binary off1 = (Binary)offSpring[0].getDecisionVariables()[0];
        Binary off2 = (Binary)offSpring[1].getDecisionVariables()[0];
               
        int size1 = offSpring[0].getNumberOfBits();
        int size2 = offSpring[1].getNumberOfBits();
        
        if (size1 != size2)
            System.err.println("The number of bits of offspring are not equals!!!");        
        else {
            for (int k = 0; k < size1; k++)
                //System.out.println("Offspring 1: " + p1.bits_.get(k) + " Offspring 2: " + p2.bits_.get(k));            
                System.out.println(off1.bits_.get(k));
            
            System.out.println(" ");

            for (int k = 0; k < size1; k++)
                //System.out.println("Offspring 1: " + p1.bits_.get(k) + " Offspring 2: " + p2.bits_.get(k));            
                System.out.println(off2.bits_.get(k));        
        }      
    }    
    /**
    * Prints the population, which is one object from SolutionSet
    * @param pop
    * @throws jmetal.util.JMException 
    * @author Matheus Giovanni Pires              
    * @email  mgpires@ecomp.uefs.br
    * @data   2014/09/17    
    */
    public void printPopulation(SolutionSet pop) throws JMException {       
        /*
        // This code works when the SolutionType is one ArrayInt
        int sizeOfPopulation = pop.getMaxSize();
        int numberOfVariables = problem_.getNumberOfVariables();
        int [] x = new int[numberOfVariables];               
        
        for (int i = 0; i < sizeOfPopulation; i++) {
            XInt vars = new XInt(pop.get(i));   
            
            System.out.print("Individual " + i + " --- ");
            for (int j = 0; j < numberOfVariables; j++) {
                x[j] = vars.getValue(j);
                System.out.print(x[j] + " ");              
            } 
            System.out.println(" ");            
        } */      
        if (problem_.getSolutionType().getClass() == ArrayRealAndBinarySolutionType.class) {       
            int sizeOfPopulation = pop.getMaxSize();
            int size;            
            for (int i = 0; i < sizeOfPopulation; i++) {   
                System.out.println("Chromosome " + i);                 
                Binary bits = (Binary)pop.get(i).getDecisionVariables()[1];                              
                ArrayReal vars = (ArrayReal)pop.get(i).getDecisionVariables()[0];                                      
                size = vars.getLength();
                for (int j = 0; j < size; j++)
                    System.out.println(vars.getValue(j));
                
                size = bits.getNumberOfBits();
                // Printing the binary part of the chromosome
                for (int j = 0; j < size; j++)
                    System.out.println(bits.getIth(j));
            }            
        }
        else if (problem_.getSolutionType().getClass() == BinarySolutionType.class) {
            int sizeOfPopulation = pop.getMaxSize();            
            int size;            
            for (int i = 0; i < sizeOfPopulation; i++) {                   
                System.out.println("Chromosome " + i);
                for (int j = 0; j < pop.get(i).getDecisionVariables().length; j++) {
                    System.out.println("Variable " + j);
                    Binary bits = (Binary)pop.get(i).getDecisionVariables()[j];                   
                    size = bits.getNumberOfBits();                
                        for (int k = 0; k < size; k++)
                            System.out.println(bits.getIth(k));
                }
            }             
        }
        else if (problem_.getSolutionType().getClass() == ArrayBinarySolutionType.class) {            
            int sizeOfPopulation = pop.getMaxSize(); 
            int size;
            for (int i = 0; i < sizeOfPopulation; i++) {   
                System.out.println("Chromosome " + i);            
                Binary bits = (Binary)pop.get(i).getDecisionVariables()[0];
                size = bits.getNumberOfBits();                
                    for (int k = 0; k < size; k++)
                        System.out.println(bits.getIth(k));            
            }
        }
        else {
            System.out.println("Print population error: solution type " + problem_.getSolutionType().getClass() + " invalid");
            System.exit(-1);
        }
  } // end printPopulation
} // NSGAII_WangMendelRuleSelectionTuningBD