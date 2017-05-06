package mgpires.metaheuristics.nsgaII;

import java.io.IOException;
import java.util.HashMap;
import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.encodings.variable.Binary;
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

public class NSGAII_SelectInstances_and_LearningKB_Backup {
    /**
    * This class was created to execute the selection of samples, and next, to
    * learning the knowledge base (rules and tuning membership functions)
    * @param args line commands
    * @throws JMException 
    * @throws IOException 
    * @throws SecurityException
    * @throws ClassNotFoundException    
    */   
    private Problem   problem   ; // The problem to solve
    private Algorithm algorithm ; // The algorithm to use
        
    // opertators to SelectInstances problem
    private Operator  crossover ; 
    private Operator  mutation  ;         
        
    // operators to LearningKB problem
    private Operator  crossoverRules, crossoverFunctions;
    private Operator  mutationAddRules, mutationChangeRules, mutationChangeFunctions;        
        
    // operator to SelectInstances and LearningKB problems
    private Operator selection ;         
    private HashMap  parameters ;
    
    private String datasetLocation, datasetName, configFile;    
    
    private Samples samples;
    private PartitionFuzzy partition;
    
    private final String stratificationDataset;
    
    int indexFold;
    
    // parameters AGMO SelectInstances
    private int populationSizeSelectInstances, maxEvaluationsSelectInstances;
    private double probabilityCrossoverSelectInstances, probabilityMutationSelectInstances;
    
    // parameters AGMO LearningKB    
    private int minNumberRules, maxNumberRules;
    private int populationSizeLearningKB, maxEvaluationsLearningKB, numberMaxToMutationChangeRulesLearningKB;
    
    private double probabilityCrossoverRulesLearningKB, 
                   probabilityCrossoverFunctionsLearningKB,
                   probabilityMutationRulesLearningKB,
                   probabilityMutationAddRulesLearningKB,
                   probabilityMutationChangeRulesLearningKB,
                   probabilityMutationChangeFunctionsLearningKB;

    public NSGAII_SelectInstances_and_LearningKB_Backup(String datasetLocation, String databaseName, String configFile, String stratificationDataset, int indexFold, int populationSizeSelectInstances, int maxEvaluationsSelectInstances, double probabilityCrossoverSelectInstances, double probabilityMutationSelectInstances, int minNumberRules, int maxNumberRules, int populationSizeLearningKB, int maxEvaluationsLearningKB, int numberMaxToMutationChangeRulesLearningKB, double probabilityCrossoverRulesLearningKB, double probabilityCrossoverFunctionsLearningKB, double probabilityMutationRulesLearningKB, double probabilityMutationAddRulesLearningKB, double probabilityMutationChangeRulesLearningKB, double probabilityMutationChangeFunctionsLearningKB) {
        this.datasetLocation = datasetLocation;
        this.datasetName = databaseName;
        this.configFile = configFile;
        this.stratificationDataset = stratificationDataset;
        this.indexFold = indexFold;
        this.populationSizeSelectInstances = populationSizeSelectInstances;
        this.maxEvaluationsSelectInstances = maxEvaluationsSelectInstances;
        this.probabilityCrossoverSelectInstances = probabilityCrossoverSelectInstances;
        this.probabilityMutationSelectInstances = probabilityMutationSelectInstances;
        this.minNumberRules = minNumberRules;
        this.maxNumberRules = maxNumberRules;
        this.populationSizeLearningKB = populationSizeLearningKB;
        this.maxEvaluationsLearningKB = maxEvaluationsLearningKB;
        this.numberMaxToMutationChangeRulesLearningKB = numberMaxToMutationChangeRulesLearningKB;
        this.probabilityCrossoverRulesLearningKB = probabilityCrossoverRulesLearningKB;
        this.probabilityCrossoverFunctionsLearningKB = probabilityCrossoverFunctionsLearningKB;
        this.probabilityMutationRulesLearningKB = probabilityMutationRulesLearningKB;
        this.probabilityMutationAddRulesLearningKB = probabilityMutationAddRulesLearningKB;
        this.probabilityMutationChangeRulesLearningKB = probabilityMutationChangeRulesLearningKB;
        this.probabilityMutationChangeFunctionsLearningKB = probabilityMutationChangeFunctionsLearningKB;
    }  
    
    public double[] execute() throws JMException, ClassNotFoundException {       
        
        samples = new Samples();              
        samples.loadSamples(datasetLocation + datasetName, stratificationDataset, indexFold);
        /* I could put this information in configFile, but, at moment, this is 
         being done here :)
         There are two options for TypeDataSet: classification or regression
         There are two options for TypeProcedure: training or test
        */
        samples.setTypeDataSet("classification");
        samples.setTypeProcedure("training");      
        
        partition = new PartitionFuzzy();
        partition.createPartition(datasetLocation + configFile);         
        
        /* ---------------------------------------------------------------------
           Part 1: Selecting instances
        ----------------------------------------------------------------------*/                        
        // dist gets the distance values among training samples
        //Map<String,Object> distance;       
        //distance = samples.getEuclideanDistance();
        double[][] distance;
        distance = samples.getEuclideanDistanceMatrix("train");
        
        problem = new SelectInstances("ArrayBinarySolutionType", samples, distance);
        
        System.out.println("Problem name....................: " + problem.getName());
        System.out.println("Database name...................: " + datasetName);
        System.out.println("Number of variables.............: " + problem.getNumberOfVariables());
        System.out.println("Type of solution................: " + problem.getSolutionType().getClass());               
        
        algorithm = new NSGAII_SelectInstances(problem);  
        
        algorithm.setInputParameter("populationSize",populationSizeSelectInstances);         
        System.out.println("Population size.................: " + algorithm.getInputParameter("populationSize").toString());
        algorithm.setInputParameter("maxEvaluations",maxEvaluationsSelectInstances);    
        System.out.println("Max evaluations.................: " + algorithm.getInputParameter("maxEvaluations").toString());
    
        parameters = new HashMap();
        parameters.put("probability", probabilityCrossoverSelectInstances);        
        crossover = CrossoverFactory.getCrossoverOperator("HUXCrossover", parameters);   
    
        parameters = new HashMap();        
        parameters.put("probability", probabilityMutationSelectInstances);        
        mutation = MutationFactory.getMutationOperator("BitFlipMutation", parameters);                    
        
        parameters = null ;
        selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters) ;                           

        algorithm.addOperator("crossover",crossover);
        algorithm.addOperator("mutation",mutation);
        algorithm.addOperator("selection",selection);

        // Execute the Algorithm
        long initTime = System.currentTimeMillis();
        SolutionSet population = algorithm.execute();
        long estimatedTime = System.currentTimeMillis() - initTime; 
        
        double aux;        
        aux = estimatedTime / 1000; // converted in seconds
        aux = aux / 60;   // converted in minutes
        
        System.out.println("\nTime to select instances.: " + aux + " minutes.");
        System.out.println("Number of solutions......: " + population.size()); 
        
        int evaluations = ((Integer)algorithm.getOutputParameter("evaluations"));
        System.out.println("Required evaluations.....: " + evaluations);
                
        population.printVariablesToFile("VAR_SelectInstances");        
        population.printObjectivesToFile("FUN_SelectInstances");
        
        getSolutionFromParetoSelectInstances(population, samples);
        samples.printSelectedSamples();
        /* ---------------------------------------------------------------------
           End of part 1: Selecting instances
        ----------------------------------------------------------------------*/ 
        
        /* ---------------------------------------------------------------------
           Part 2: Learning KB
        ----------------------------------------------------------------------*/         
        problem = new LearningKB("ArrayIntAndRealSolutionType", samples, partition,
            minNumberRules, maxNumberRules);
        
        System.out.println("Problem name....................: " + problem.getName());
        System.out.println("Database name...................: " + datasetName);
        System.out.println("Number of variables.............: " + problem.getNumberOfVariables());
        System.out.println("Type of solution................: " + problem.getSolutionType().getClass());
        
        algorithm = new NSGAII_LearningKB(problem); 
        
        algorithm.setInputParameter("populationSize", populationSizeLearningKB);        
        System.out.println("Population size.................: " + algorithm.getInputParameter("populationSize").toString());
        algorithm.setInputParameter("maxEvaluations", maxEvaluationsLearningKB);    
        System.out.println("Max evaluations.................: " + algorithm.getInputParameter("maxEvaluations").toString());
    
        algorithm.setInputParameter("probabilityMutationRules", probabilityMutationRulesLearningKB);
        
        parameters = new HashMap();
        parameters.put("probability", probabilityCrossoverRulesLearningKB);                
        crossoverRules = CrossoverFactory.getCrossoverOperator("SinglePointCrossoverToRules", parameters); 
        
        parameters = new HashMap();
        parameters.put("probability", probabilityCrossoverFunctionsLearningKB);
        crossoverFunctions = CrossoverFactory.getCrossoverOperator("BLXAlphaCrossover", parameters);
    
        parameters = new HashMap() ;                
        parameters.put("probability", probabilityMutationAddRulesLearningKB);
        mutationAddRules = MutationFactory.getMutationOperator("MutationAddRules", parameters);
        
        parameters = new HashMap() ;        
        parameters.put("probability", probabilityMutationChangeRulesLearningKB) ;            
        parameters.put("numberMaxToMutation", numberMaxToMutationChangeRulesLearningKB) ;
        mutationChangeRules = MutationFactory.getMutationOperator("MutationChangeRules", parameters);                            
        
        parameters = new HashMap() ;        
        parameters.put("probability", probabilityMutationChangeFunctionsLearningKB) ;                    
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
        
        aux = estimatedTime / 1000; // converted in seconds
        aux = aux / 60;   // converted in minutes
        
        System.out.println("\nTime to learning KB..: " + aux + " minutes.");
        System.out.println("Number of solutions..: " + population.size()); 
        
        evaluations = ((Integer)algorithm.getOutputParameter("evaluations"));
        System.out.println("Required evaluations.: " + evaluations);
        
        population.printVariablesToFile("VAR_LearningKB");        
        population.printObjectivesToFile("FUN_LearningKB");       
        
        Solution finalSolution = getSolutionFromParetoLearningKB(population);
        System.out.println("\nPrinting final solution...");
        ((NSGAII_LearningKB)algorithm).printOffSpring(finalSolution);      
        /* ---------------------------------------------------------------------
           End of part 2: Learning KB
        ----------------------------------------------------------------------*/ 
        
        /* ---------------------------------------------------------------------
           Part 3: Test
        ----------------------------------------------------------------------*/
        double accuracy = FuzzyReasoning.classicFuzzyReasoning(finalSolution, samples, partition, "test");
        
        int numberOfConditions = Rules.calcNumberOfConditions(finalSolution);
        
        System.out.println("Test result. Accuracy = " + accuracy + " Complexity = " + numberOfConditions);
        
        /* ---------------------------------------------------------------------
           End of part 3: Test
        ----------------------------------------------------------------------*/  
        double[] result = new double[2];
        result[0] = accuracy;
        result[1] = (double)numberOfConditions;
        
        return result;
        
    } // end of execute method

    /**
     * This method get one solution from Pareto front. The criterion used was
     * the best accuracy (the first objective of the SelecInstances problem)
     * @param population It contain the solutions from Pareto front
     * @param samples It will be used to encode the instances selected, 
     * according to the solution gotten from Pareto front
     */
    private static void getSolutionFromParetoSelectInstances(SolutionSet population, Samples samples) {
        int numberOfSolutions, index, bits, count;        
        double max;
        Binary sol;        
        /* max will store the higher value of accuracy (objective zero)
         as the jMetal code minimizes the objectives, when is necessary to maximize, 
         must multiplicate the objective by -1. So, because of this, the code below does < max
        */
        max = population.get(0).getObjective(0); 
        numberOfSolutions = population.size();
        index = 0;
        for (int i = 0; i < numberOfSolutions; i++) {            
            if (population.get(i).getObjective(0) < max) {
                max = population.get(i).getObjective(0);
                index = i;
            }
        }
        
        sol = (Binary)population.get(index).getDecisionVariables()[0];
        bits = sol.getNumberOfBits();        
        count = 0;
        for (int i = 0; i < bits; i++) {
            if (sol.getIth(i) == true) {
                samples.setSelectedSamples(i, true);                
                count++;
            }
        }    
        samples.setNumberOfSelectedSamples(count);
    } // end getSolutionFromPareto method
    
    /**
     * This method get one solution from Pareto front. The criterion used was
     * the best accuracy (the first objective of the LearningKB problem)
     * @param population It contain the solutions from Pareto front     
     */
    private static Solution getSolutionFromParetoLearningKB(SolutionSet population) {
        int numberOfSolutions = population.size(), index = 0;        
        double max = population.get(0).getObjective(0);        
                
        /* max will store the higher value of accuracy (objective zero)
         as the jMetal code minimizes the objectives, when is necessary to maximize, 
         must multiplicate the objective by -1. So, because of this, the code below does < max
        */
        for (int i = 0; i < numberOfSolutions; i++) {            
            if (population.get(i).getObjective(0) < max) {
                max = population.get(i).getObjective(0);
                index = i;
            }
        }
        
        return population.get(index);
      
    } // end of getSolutionFromPareto method

} // end of NSGAII_SelectInstances_and_LearningKB class