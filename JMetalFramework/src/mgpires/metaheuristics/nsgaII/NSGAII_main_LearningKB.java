    package mgpires.metaheuristics.nsgaII;

import java.io.IOException;
import java.util.HashMap;
import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.SelectionFactory;
import jmetal.util.JMException;
import mgpires.core.PartitionFuzzy;
import mgpires.core.Samples;
import mgpires.problems.LearningKB;
import mgpires.solutionType.ArrayIntAndRealSolutionType;

public class NSGAII_main_LearningKB {
    /**
    * This class was created to test the codes to select instances
    * @param args line commands
    * @throws JMException 
    * @throws IOException 
    * @throws SecurityException
    * @throws ClassNotFoundException    
    */
    public static void main(String[] args) throws 
                                  JMException, 
                                  SecurityException, 
                                  IOException, 
                                  ClassNotFoundException {
        Problem   problem   ; // The problem to solve
        Algorithm algorithm ; // The algorithm to use
        Operator  crossoverRules, crossoverFunctions ; // Crossover operator
        Operator  mutationAddRules, mutationChangeRules, mutationChangeFunctions ; // Mutation operator
        Operator  selection ; // Selection operator    
        HashMap  parameters ; // Operator parameters        
        
        String datasetName     = "iris";
        String datasetLocation = "./dataset-test/" + datasetName + "/"; 
        //String datasetLocation = "./dataset-test/" + datasetName + "/";
        String configFile      = "config-" + datasetName + ".txt";
         
        Samples samples = new Samples();              
        samples.loadSamples(datasetLocation + datasetName, "10", 1); 
        samples.printTraSamples();
        samples.printTestSamples();
        // There are two options: classification or regression
        samples.setTypeDataSet("classification");
        
        // "trainingKB" means that will be executed only LearningKB. The selection of 
        // instances not
        samples.setTypeProcedure("trainingKB");
        
        PartitionFuzzy partition;
        partition = new PartitionFuzzy();
        partition.createPartition(datasetLocation + configFile); 
        partition.printPartitionFuzzyInput();
        partition.printPartitionFuzzyOutput();
        
        // minNumberRules and maxNumberRules must be defined by the user
        int minNumberRules = 5, maxNumberRules = 30;
        
        problem = new LearningKB("ArrayIntAndRealSolutionType", samples, partition,
            minNumberRules, maxNumberRules);        
    
        // printing some data about the problem
        System.out.println("Problem name....................: " + problem.getName());
        System.out.println("Database name...................: " + datasetName);
        System.out.println("Number of variables.............: " + problem.getNumberOfVariables());
        System.out.println("Type of solution................: " + problem.getSolutionType().getClass());            
        /*
        System.out.print  ("Printing the length of variables: ");
        for (int i = 0; i < problem.getNumberOfVariables(); i++) {
            System.out.print(problem.getLength(i) + " ");
        }        
        System.out.println(" ");  
        */
        
        /*
        int length = ((ArrayIntAndRealSolutionType)problem.getSolutionType()).getArrayIntLowerBounds_().length;        
        double[] lowerBounds = ((ArrayIntAndRealSolutionType)problem.getSolutionType()).getArrayIntLowerBounds_();
        double[] upperBounds = ((ArrayIntAndRealSolutionType)problem.getSolutionType()).getArrayIntUpperBounds_();        
        System.out.println("Lower and upper bounds of the variables");
        for (int i = 0; i < length; i++) {
            System.out.println(lowerBounds[i] + " " + upperBounds[i]);
        }*/        
        
        // testing other form to copy array
        double[] lowerBounds = new double[problem.getNumberOfVariables()];
        double[] upperBounds = new double[problem.getNumberOfVariables()];
        System.arraycopy(((ArrayIntAndRealSolutionType)problem.getSolutionType()).getArrayIntLowerBounds_(), 0, 
                lowerBounds, 0, problem.getNumberOfVariables());
        
        System.arraycopy(((ArrayIntAndRealSolutionType)problem.getSolutionType()).getArrayIntUpperBounds_(), 0, 
                upperBounds, 0, problem.getNumberOfVariables());
        
        System.out.println("Printing the limits values of the variables...");
        for (int i = 0; i < problem.getNumberOfVariables(); i++) {
            System.out.println(lowerBounds[i] + " " + upperBounds[i]);
        }        
      
        algorithm = new NSGAII_LearningKB(problem);                     
        
        algorithm.setInputParameter("populationSize",100);        
        System.out.println("Population size.................: " + algorithm.getInputParameter("populationSize").toString());
      
        algorithm.setInputParameter("maxEvaluations",1000);    
        System.out.println("Max evaluations.................: " + algorithm.getInputParameter("maxEvaluations").toString());        
        
        algorithm.setInputParameter("probabilityMutationRules", 0.1);
        
        parameters = new HashMap();
        parameters.put("probability", 0.6);        
        crossoverRules = CrossoverFactory.getCrossoverOperator("SinglePointCrossoverToRules", parameters);   
        
        parameters = new HashMap();
        parameters.put("probability", 0.5);
        crossoverFunctions = CrossoverFactory.getCrossoverOperator("BLXAlphaCrossover", parameters);
                
        parameters = new HashMap();        
        parameters.put("probability", 0.55);
        mutationAddRules = MutationFactory.getMutationOperator("MutationAddRules", parameters);
        
        parameters = new HashMap() ;        
        parameters.put("probability", 0.45) ;            
        parameters.put("numberMaxToMutation", 5) ;
        mutationChangeRules = MutationFactory.getMutationOperator("MutationChangeRules", parameters);                            
        
        parameters = new HashMap() ;        
        parameters.put("probability", 0.2) ;                    
        mutationChangeFunctions = MutationFactory.getMutationOperator("MutationChangeFunctions", parameters);        
        
        parameters = null ;
        selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters) ;
        
        algorithm.addOperator("crossoverRules",crossoverRules);
        algorithm.addOperator("crossoverFunctions",crossoverFunctions);
        algorithm.addOperator("mutationAddRules",mutationAddRules);
        algorithm.addOperator("mutationChangeRules",mutationChangeRules);
        algorithm.addOperator("mutationChangeFunctions",mutationChangeFunctions);        
        algorithm.addOperator("selection",selection);
        
        // Execute the Algorithm
        long initTime = System.currentTimeMillis();
        
        SolutionSet population = algorithm.execute();        
        
        double estimatedTime = System.currentTimeMillis() - initTime;                
        double aux = estimatedTime * 0.001; // converted in seconds
        double timeLearningKB = aux * 0.0167;  // converted in minutes
        double timeLearningKB2 = aux / 60.0;  // converted in minutes
        
        System.out.println("\nTime to learning KB.: " + aux + " seconds. " + timeLearningKB + " minutes. " + timeLearningKB2 + " minutes.");
        System.out.println("Number of solutions.: " + population.size());   
        
        int evaluations = ((Integer)algorithm.getOutputParameter("evaluations"));
        System.out.println("Required evaluations: " + evaluations);
        
        population.printVariablesToFile("VAR_LearningKB");        
        population.printObjectivesToFile("FUN_LearningKB");        
        
        //Solution finalSolution = ((LearningKB)problem).getSolutionFromPareto(population);
        Solution finalSolution = ((LearningKB)problem).getMidPointSolutionFromPareto(population);
        
        ((NSGAII_LearningKB)algorithm).printOffSpring(finalSolution);      
        
      
    } // end of main method

} // end NSGAII_main_LearningKB class