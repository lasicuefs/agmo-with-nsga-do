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
import mgpires.algorithms.Rules;
import mgpires.core.FuzzyReasoning;
import mgpires.core.PartitionFuzzy;
import mgpires.core.Samples;
import mgpires.problems.LearningKB;
import mgpires.problems.SelectInstances;

public class NSGAII_main_SelectInstances_and_LearningKB {
    /**
    * This class was created to execute the selection of samples, and next, to
    * learning the knowledge base (rules and tuning membership functions)
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
        
        // opertators to SelectInstances problem
        Operator  crossover ; 
        Operator  mutation  ;         
        
        // operators to LearningKB problem
        Operator  crossoverRules, crossoverFunctions;
        Operator  mutationAddRules, mutationChangeRules, mutationChangeFunctions;        
        
        // operator to SelectInstances and LearningKB problems
        Operator  selection ;         
        HashMap  parameters ;
        
        String datasetName     = "cleveland";
        String datasetLocation = "./dataset/" + datasetName + "/";
        String configFile      = "config-" + datasetName + ".txt";
        String pathResult      = "./result/" + datasetName + "/";        
        
        Samples samples = new Samples();              
        samples.loadSamples(datasetLocation + datasetName, "10", 1);
        samples.printTraSamples();
        samples.printTestSamples();
  
        samples.setTypeDataSet("classification");
        samples.setTypeProcedure("training");        
        
        PartitionFuzzy partition;
        partition = new PartitionFuzzy();
        partition.createPartition(datasetLocation + configFile);     
        partition.printPartitionFuzzyInput();
        partition.printPartitionFuzzyOutput();
        
        /* ---------------------------------------------------------------------
           Part 1: Selecting instances
        ----------------------------------------------------------------------*/                        
        // dist gets the distance values among training samples
        //Map<String,Object> distance;       
        //distance = samples.getEuclideanDistance();        
        double[][] distance;
        distance = samples.getEuclideanDistanceMatrix("train");
        
        problem = new SelectInstances("ArrayBinarySolutionType", samples, distance);
        // printing some data about the problem
        System.out.println("Problem name....................: " + problem.getName());
        System.out.println("Database name...................: " + datasetName);
        System.out.println("Number of variables.............: " + problem.getNumberOfVariables());
        System.out.println("Type of solution................: " + problem.getSolutionType().getClass());               
        
        algorithm = new NSGAII_SelectInstances(problem); 
        
        algorithm.setInputParameter("populationSize",10);        
        System.out.println("Population size.................: " + algorithm.getInputParameter("populationSize").toString());
        algorithm.setInputParameter("maxEvaluations",1000);    
        System.out.println("Max evaluations.................: " + algorithm.getInputParameter("maxEvaluations").toString());
    
        parameters = new HashMap();
        parameters.put("probability", 0.9);        
        crossover = CrossoverFactory.getCrossoverOperator("HUXCrossover", parameters);   
    
        parameters = new HashMap();        
        parameters.put("probability", 0.2);        
        mutation = MutationFactory.getMutationOperator("BitFlipMutation", parameters);                    
        
        parameters = null ;
        selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters) ;                           

        // Add the operators to the algorithm
        algorithm.addOperator("crossover",crossover);
        algorithm.addOperator("mutation",mutation);
        algorithm.addOperator("selection",selection);

        // Execute the Algorithm
        long initTime = System.currentTimeMillis();
        
        SolutionSet population = algorithm.execute();
        
        double estimatedTime = System.currentTimeMillis() - initTime;                
        double aux = estimatedTime * 0.001; // converted in seconds
        double timeSelectInstances = aux * 0.0167;  // converted in minutes
        
        System.out.println("\nTime to select instances.: " + timeSelectInstances + " minutes.");
        System.out.println("Number of solutions......: " + population.size()); 
        
        int evaluations = ((Integer)algorithm.getOutputParameter("evaluations"));
        System.out.println("Required evaluations.....: " + evaluations);
                
        population.printVariablesToFile(pathResult + "VAR_SelectInstances");        
        population.printObjectivesToFile(pathResult + "FUN_SelectInstances");
        
        double reductionRate = ((SelectInstances)problem).getSolutionFromPareto(population);
        
        System.out.println("Reduction rate = " + reductionRate);
        samples.printSelectedSamples();
        /* ---------------------------------------------------------------------
           End of part 1: Selecting instances
        ----------------------------------------------------------------------*/ 
        
        /* ---------------------------------------------------------------------
           Part 2: Learning KB
        ----------------------------------------------------------------------*/         
         // minNumberRules and maxNumberRules must be defined by the user
        int minNumberRules = 5, maxNumberRules = 30;
        
        problem = new LearningKB("ArrayIntAndRealSolutionType", samples, partition,
            minNumberRules, maxNumberRules);
        
        // printing some data about the problem
        System.out.println("Problem name....................: " + problem.getName());
        System.out.println("Database name...................: " + datasetName);
        System.out.println("Number of variables.............: " + problem.getNumberOfVariables());
        System.out.println("Type of solution................: " + problem.getSolutionType().getClass());
        
        algorithm = new NSGAII_LearningKB(problem); 
        
        algorithm.setInputParameter("populationSize",100);        
        System.out.println("Population size.................: " + algorithm.getInputParameter("populationSize").toString());
        algorithm.setInputParameter("maxEvaluations",10000);    
        System.out.println("Max evaluations.................: " + algorithm.getInputParameter("maxEvaluations").toString());
        
        algorithm.setInputParameter("probabilityMutationRules", 0.1);
    
        parameters = new HashMap();
        parameters.put("probability", 0.6);                
        crossoverRules = CrossoverFactory.getCrossoverOperator("SinglePointCrossoverToRules", parameters); 
        
        parameters = new HashMap();
        parameters.put("probability", 0.5);
        crossoverFunctions = CrossoverFactory.getCrossoverOperator("BLXAlphaCrossover", parameters);
    
        parameters = new HashMap() ;                
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
        
        algorithm.addOperator("crossoverRules", crossoverRules);
        algorithm.addOperator("crossoverFunctions", crossoverFunctions);
        algorithm.addOperator("mutationAddRules", mutationAddRules);
        algorithm.addOperator("mutationChangeRules", mutationChangeRules);
        algorithm.addOperator("mutationChangeFunctions", mutationChangeFunctions);        
        algorithm.addOperator("selection", selection);

        // Execute the Algorithm
        initTime = System.currentTimeMillis();
        
        population = algorithm.execute();
        
        estimatedTime = System.currentTimeMillis() - initTime;       
        aux = estimatedTime * 0.001; // converted in seconds
        double timeLearningKB = aux * 0.0167;  // converted in minutes
        
        System.out.println("\nTime to learning KB..: " + timeLearningKB + " minutes.");
        System.out.println("Number of solutions..: " + population.size()); 
        
        evaluations = ((Integer)algorithm.getOutputParameter("evaluations"));
        System.out.println("Required evaluations.: " + evaluations);
        
        population.printVariablesToFile(pathResult + "VAR_LearningKB");        
        population.printObjectivesToFile(pathResult + "FUN_LearningKB");       
        
        //Solution finalSolution = ((LearningKB)problem).getSolutionFromPareto(population);
        Solution finalSolution = ((LearningKB)problem).getMidPointSolutionFromPareto(population);
        
        // accuracyTra is the accuracy on the training
        double accuracyTra = finalSolution.getObjective(0);
        
        System.out.println("\nPrinting final solution...");
        
        ((NSGAII_LearningKB)algorithm).printOffSpring(finalSolution);      
        /* ---------------------------------------------------------------------
           End of part 2: Learning KB
        ----------------------------------------------------------------------*/ 
        
        /* ---------------------------------------------------------------------
           Part 3: Test
        ----------------------------------------------------------------------*/
        double accuracyTest = FuzzyReasoning.classicFuzzyReasoning(finalSolution, samples, partition, "test");
        
        int complexity = Rules.calcNumberOfConditions(finalSolution);        
        
        System.out.println("Training Accuracy = " + accuracyTra + " Test Accuracy = " + 
           accuracyTest + " Complexity = " + complexity + " Time Select Instances = " + timeSelectInstances + 
           " Time LearningKB = " + timeLearningKB); 
        
        /* ---------------------------------------------------------------------
           End of part 3: Test
        ----------------------------------------------------------------------*/        
        
    } // end of main method  
    
} // end of NSGAII_main_SelectInstances_and_LearningKB class