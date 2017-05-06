//  NSGAII_main_WangMendelRuleSelectionTuningBD.java
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

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.SolutionSet;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.selection.SelectionFactory;
import jmetal.problems.ProblemFactory;
import mgpires.problems.WangMendelRuleSelectionTuningBD;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.Configuration;
import jmetal.util.JMException;

/** 
 * Class to configure and execute the NSGA-II algorithm.  
 *     
 * Besides the classic NSGA-II, a steady-state version (ssNSGAII) is also
 * included (See: J.J. Durillo, A.J. Nebro, F. Luna and E. Alba 
 *                  "On the Effect of the Steady-State Selection Scheme in 
 *                  Multi-Objective Genetic Algorithms"
 *                  5th International Conference, EMO 2009, pp: 183-197. 
 *                  April 2009)
 */ 

public class NSGAII_main_WangMendelRuleSelectionTuningBD {
  public static Logger      logger_ ;      // Logger object
  public static FileHandler fileHandler_ ; // FileHandler object

  /**
   * @param args Command line arguments.
   * @throws JMException 
   * @throws IOException 
   * @throws SecurityException
   * @throws ClassNotFoundException
   * Usage: three options
      - jmetal.metaheuristics.nsgaII.NSGAII_main_WangMendelRuleSelectionTuningBD
      - jmetal.metaheuristics.nsgaII.NSGAII_main_WangMendelRuleSelectionTuningBD problemName
      - jmetal.metaheuristics.nsgaII.NSGAII_main_WangMendelRuleSelectionTuningBD problemName paretoFrontFile
   */
  public static void main(String [] args) throws 
                                  JMException, 
                                  SecurityException, 
                                  IOException, 
                                  ClassNotFoundException {
    Problem   problem   ; // The problem to solve
    Algorithm algorithm ; // The algorithm to use
    Operator  crossover ; // Crossover operator
    Operator  mutation  ; // Mutation operator
    Operator  selection ; // Selection operator
    
    HashMap  parameters ; // Operator parameters
    
    QualityIndicator indicators ; // Object to get quality indicators

    // Logger object and file to store log messages
    logger_      = Configuration.logger_ ;
    fileHandler_ = new FileHandler("NSGAII_main.log"); 
    logger_.addHandler(fileHandler_) ;
        
    indicators = null ;
    if (args.length == 1) {
      Object [] params = {"Real"};
      problem = (new ProblemFactory()).getProblem(args[0],params);
    } // if
    else if (args.length == 2) {
      Object [] params = {"Real"};
      problem = (new ProblemFactory()).getProblem(args[0],params);
      indicators = new QualityIndicator(problem, args[1]) ;
    } // if
    else { // Default problem               
    
        /* 
        ArrayRealAndBinary is the codification type (SolutionType) of the chromossome
        configIris.txt is the file with information about variables and
        partition fuzzy
        iris-tra.txt is the training file
        iris-test.txt is the test file
        */
        //problem = new WangMendelRuleSelectionTuningBD("ArrayRealAndBinary", "configIris.txt", 
            //"iris", 5, 1);
        
        //problem = new WangMendelRuleSelectionTuningBD("BinarySolutionType", "configIris.txt", 
            //"iris", 5, 1);       
        
         problem = new WangMendelRuleSelectionTuningBD("ArrayBinarySolutionType", "configIris.txt", 
            "iris", 5, 1); 
      
    } // else
    
    // printing some data about the problem
    System.out.println("Problem name........: " + problem.getName());
    System.out.println("Number of variables.: " + problem.getNumberOfVariables());
    System.out.println("Type of solution....: " + problem.getSolutionType().getClass());
    System.out.println(" ");
    
    System.out.println("Printing the length of variables");
    for (int i = 0; i < problem.getNumberOfVariables(); i++) {        
        System.out.println(problem.getLength(i));
    }    
    
    algorithm = new NSGAII_WangMendelRuleSelectionTuningBD(problem); 
    
    // Algorithm parameters
    algorithm.setInputParameter("populationSize",2);
    algorithm.setInputParameter("maxEvaluations",100);    
    
    // Mutation and Crossover for ArrayRealAndBinarySolutionType codification 
    parameters = new HashMap() ;
    parameters.put("probability", 0.9) ;
    parameters.put("distributionIndex", 20.0) ;    
    //crossover = CrossoverFactory.getCrossoverOperator("SBXSinglePointCrossover", parameters);        
    crossover = CrossoverFactory.getCrossoverOperator("HUXCrossover", parameters);
    
    
    //parameters = new HashMap() ;
    //parameters.put("probability", 1.0/problem.getNumberOfVariables()) ;
    //parameters.put("distributionIndex", 20.0) ;
    //mutation = MutationFactory.getMutationOperator("PolynomialBitFlipMutation", parameters);                    

    // Selection Operator 
    parameters = null ;
    selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters) ;                           

    // Add the operators to the algorithm
    algorithm.addOperator("crossover",crossover);
    //algorithm.addOperator("mutation",mutation);
    algorithm.addOperator("selection",selection);

    // Add the indicator object to the algorithm
    algorithm.setInputParameter("indicators", indicators) ;
    
    // Execute the Algorithm
    long initTime = System.currentTimeMillis();
    SolutionSet population = algorithm.execute();
    long estimatedTime = System.currentTimeMillis() - initTime;
    
    // Result messages 
    logger_.info("Total execution time: "+estimatedTime + "ms");
    logger_.info("Variables values have been writen to file VAR");
    population.printVariablesToFile("VAR");    
    logger_.info("Objectives values have been writen to file FUN");
    population.printObjectivesToFile("FUN");
  
    if (indicators != null) {
      logger_.info("Quality indicators") ;
      logger_.info("Hypervolume: " + indicators.getHypervolume(population)) ;
      logger_.info("GD         : " + indicators.getGD(population)) ;
      logger_.info("IGD        : " + indicators.getIGD(population)) ;
      logger_.info("Spread     : " + indicators.getSpread(population)) ;
      logger_.info("Epsilon    : " + indicators.getEpsilon(population)) ;  
     
      int evaluations = ((Integer)algorithm.getOutputParameter("evaluations")).intValue();
      logger_.info("Speed      : " + evaluations + " evaluations") ;     
    } // if   
  } //main
} // NSGAII_main_WangMendelRuleSelectionTuningBD
