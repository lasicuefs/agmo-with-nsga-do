package mgpires.metaheuristics.nsgaII;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Callable;
import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.SelectionFactory;
import jmetal.util.JMException;
import mgpires.algorithms.Printer;
import mgpires.algorithms.Rules;
import mgpires.core.FuzzyReasoning;
import mgpires.core.PartitionFuzzy;
import mgpires.core.Samples;
import mgpires.problems.LearningKB;

public class NSGAII_LearningKB_Thread implements Callable {
    
    private Problem   problem   ; // The problem to solve
    private Algorithm algorithm ; // The algorithm to use
        
    // operators to LearningKB problem
    private Operator  crossoverRules, crossoverFunctions;
    private Operator  mutationAddRules, mutationChangeRules, mutationChangeFunctions;        
    private Operator selection ;         
    private HashMap  parameters ;
    
    private final String datasetLocation, datasetName, configFile, pathResult;        
    private Samples samples;
    private PartitionFuzzy partition;
    
    private final String stratificationDataset;
    
    int indexFold, indexThread;
     
    // parameters AGMO LearningKB    
    private final int minNumberRules, maxNumberRules;
    private final int populationSizeLearningKB, maxEvaluationsLearningKB, numberMaxToMutationChangeRulesLearningKB;
    
    private final double probabilityCrossoverRulesLearningKB, 
                   probabilityCrossoverFunctionsLearningKB,
                   probabilityMutationRulesLearningKB,
                   probabilityMutationAddRulesLearningKB,
                   probabilityMutationChangeRulesLearningKB,
                   probabilityMutationChangeFunctionsLearningKB;

    public NSGAII_LearningKB_Thread(String datasetLocation, String datasetName, String configFile, String pathResult, String stratificationDataset, int indexFold, int indexThread,
       int minNumberRules, int maxNumberRules, int populationSizeLearningKB, int maxEvaluationsLearningKB, int numberMaxToMutationChangeRulesLearningKB, double probabilityCrossoverRulesLearningKB, double probabilityCrossoverFunctionsLearningKB, double probabilityMutationRulesLearningKB, double probabilityMutationAddRulesLearningKB, double probabilityMutationChangeRulesLearningKB, double probabilityMutationChangeFunctionsLearningKB) {
        this.datasetLocation = datasetLocation;
        this.datasetName = datasetName;
        this.configFile = configFile;
        this.pathResult = pathResult;        
        this.stratificationDataset = stratificationDataset;
        this.indexFold = indexFold;
        this.indexThread = indexThread;
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

    @Override
    public Object call() throws Exception {
        try {
            execute();
        } catch (JMException | ClassNotFoundException | IOException ex) {
            System.err.println("NSGAII_LearningKB_Thread class > run method error: " + ex);
            System.exit(-1);
        }
        return 1;
    }
    
    public void execute() throws JMException, ClassNotFoundException, IOException {
        
        samples = new Samples();              
        samples.loadSamples(datasetLocation + datasetName, stratificationDataset, indexFold);
        samples.setTypeDataSet("classification");
        samples.setTypeProcedure("trainingKB");
        
        partition = new PartitionFuzzy();
        partition.createPartition(datasetLocation + configFile);
        
        problem = new LearningKB("ArrayIntAndRealSolutionType", samples, partition,
            minNumberRules, maxNumberRules);
        
        algorithm = new NSGAII_LearningKB(problem); 
        
        algorithm.setInputParameter("populationSize", populationSizeLearningKB);                
        algorithm.setInputParameter("maxEvaluations", maxEvaluationsLearningKB);
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
        long initTime = System.currentTimeMillis();
        
        SolutionSet population = algorithm.execute();
        
        double estimatedTime = System.currentTimeMillis() - initTime;                
        double aux = estimatedTime * 0.001; // converted in seconds
        double timeLearningKB = aux;
        
        //double timeLearningKB = aux * 0.0167;  // converted in minutes
        //double timeLearningKB = aux / 60.0;  // converted in minutes
        
        //Solution finalSolution = ((LearningKB)problem).getSolutionFromPareto(population);
        Solution finalSolution = ((LearningKB)problem).getMidPointSolutionFromPareto(population);
        
        // accuracyTra is the accuracy on the training
        double accuracyTra = finalSolution.getObjective(0);
         
        Printer.printVariablesToFile(finalSolution, pathResult + "VAR_" + Integer.toString(indexFold) + "_" + Integer.toString(indexThread));
        Printer.printObjectivesToFile(finalSolution, pathResult + "FUN_" + Integer.toString(indexFold) + "_" + Integer.toString(indexThread)); 
        
        /* ---------------------------------------------------------------------
           Test
        ----------------------------------------------------------------------*/
        double accuracyTest = FuzzyReasoning.classicFuzzyReasoning(finalSolution, samples, partition, "test");
        
        int complexity = Rules.calcNumberOfConditions(finalSolution);
        int numberOfRules = (int)finalSolution.getDecisionVariables()[2].getValue();
        
        System.out.println("\nFold = " + indexFold + " Test result. Accuracy = " + 
           accuracyTest + " Complexity = " + complexity + " #Rules = " + numberOfRules +
           " Time = " + timeLearningKB);        
        
        Printer.printResultToFile(datasetName, pathResult, indexFold, timeLearningKB, 
           (-1.0 * accuracyTra), complexity, accuracyTest, numberOfRules);
        
      
    } // end of main method

} // end NSGAII_LearningKB_Thread class