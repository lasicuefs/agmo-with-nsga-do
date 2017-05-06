package mgpires.metaheuristics.nsgaII;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
import mgpires.problems.SelectInstances;

public class NSGAII_SelectInstances_and_LearningKB implements Runnable {

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
    
    private final String datasetLocation, datasetName, configFile, pathResult;        
    private Samples samples;
    private PartitionFuzzy partition;
    
    private final String stratificationDataset;
    
    int indexFold;
    
    // parameters AGMO SelectInstances
    private final int populationSizeSelectInstances, maxEvaluationsSelectInstances;
    private final double probabilityCrossoverSelectInstances, probabilityMutationSelectInstances;
    
    // parameters AGMO LearningKB    
    private final int minNumberRules, maxNumberRules;
    private final int populationSizeLearningKB, maxEvaluationsLearningKB, numberMaxToMutationChangeRulesLearningKB;
    
    private final double probabilityCrossoverRulesLearningKB, 
                   probabilityCrossoverFunctionsLearningKB,
                   probabilityMutationRulesLearningKB,
                   probabilityMutationAddRulesLearningKB,
                   probabilityMutationChangeRulesLearningKB,
                   probabilityMutationChangeFunctionsLearningKB;

    public NSGAII_SelectInstances_and_LearningKB(String datasetLocation, String databaseName, String configFile, String stratificationDataset, 
        int indexFold, int populationSizeSelectInstances, int maxEvaluationsSelectInstances, double probabilityCrossoverSelectInstances, 
        double probabilityMutationSelectInstances, int minNumberRules, int maxNumberRules, int populationSizeLearningKB, 
        int maxEvaluationsLearningKB, int numberMaxToMutationChangeRulesLearningKB, double probabilityCrossoverRulesLearningKB, 
        double probabilityCrossoverFunctionsLearningKB, double probabilityMutationRulesLearningKB, double probabilityMutationAddRulesLearningKB, 
        double probabilityMutationChangeRulesLearningKB, double probabilityMutationChangeFunctionsLearningKB, String pathResult) {
        
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
        this.pathResult = pathResult;
    } 
    
    @Override
    public void run () {
        try {
            execute();
        } catch (JMException | ClassNotFoundException | IOException ex) {
            System.err.println("NSGAII_SelectInstances_and_LearningKB class > run method error: " + ex);
            System.exit(-1);
        }
    }   
   
    public void execute() throws JMException, ClassNotFoundException, IOException {
        
        samples = new Samples();              
        samples.loadSamples(datasetLocation + datasetName, stratificationDataset, indexFold);
        /*
         There are two options for TypeDataSet: classification or regression
         There are two options for TypeProcedure: training or test
        */
        samples.setTypeDataSet("classification");
        samples.setTypeProcedure("training");      
        
        partition = new PartitionFuzzy();
        partition.createPartition(datasetLocation + configFile);    
        //partition.printPartitionFuzzyInput();
        //partition.printPartitionFuzzyOutput();
        
        /* ---------------------------------------------------------------------
           Part 1: Selecting instances
        ----------------------------------------------------------------------*/  
        // dist gets the distance values among training samples
        //Map<String,Object> distance;       
        //distance = samples.getEuclideanDistance();
        double[][] distance;
        distance = samples.getEuclideanDistanceMatrix("train");
        
        problem = new SelectInstances("ArrayBinarySolutionType", samples, distance);        
        
        /*System.out.println("Problem name....................: " + problem.getName());
        System.out.println("Database name...................: " + datasetName);
        System.out.println("Number of variables.............: " + problem.getNumberOfVariables());
        System.out.println("Type of solution................: " + problem.getSolutionType().getClass());               
        */
        
        algorithm = new NSGAII_SelectInstances(problem);  
        
        algorithm.setInputParameter("populationSize",populationSizeSelectInstances);                 
        algorithm.setInputParameter("maxEvaluations",maxEvaluationsSelectInstances);    
        
        /*System.out.println("Population size.................: " + algorithm.getInputParameter("populationSize").toString());
        System.out.println("Max evaluations.................: " + algorithm.getInputParameter("maxEvaluations").toString());
        */
    
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
        double estimatedTime = System.currentTimeMillis() - initTime; 
        double aux = estimatedTime / 1000; // converted in seconds
        double timeSelectInstances = aux / 60;  // converted in minutes      
        
        //System.out.println("\nTime to select instances.: " + timeSelectInstances + " minutes.");
        //System.out.println("Number of solutions......: " + population.size()); 
        
        //int evaluations = ((Integer)algorithm.getOutputParameter("evaluations"));
        //System.out.println("Required evaluations.....: " + evaluations);
                
        //population.printVariablesToFile("VAR_SelectInstances_" + Integer.toString(indexFold));
        //population.printObjectivesToFile("FUN_SelectInstances_" + Integer.toString(indexFold));
        
        double reductionRate = ((SelectInstances)problem).getSolutionFromPareto(population);
        //samples.printSelectedSamples();
        /* ---------------------------------------------------------------------
           End of part 1: Selecting instances
        ----------------------------------------------------------------------*/ 
        
        /* ---------------------------------------------------------------------
           Part 2: Learning KB
        ----------------------------------------------------------------------*/         
        problem = new LearningKB("ArrayIntAndRealSolutionType", samples, partition,
            minNumberRules, maxNumberRules);
        
        /*System.out.println("Problem name....................: " + problem.getName());
        System.out.println("Database name...................: " + datasetName);
        System.out.println("Number of variables.............: " + problem.getNumberOfVariables());
        System.out.println("Type of solution................: " + problem.getSolutionType().getClass());
        */
        
        algorithm = new NSGAII_LearningKB(problem); 
        
        algorithm.setInputParameter("populationSize", populationSizeLearningKB);                
        algorithm.setInputParameter("maxEvaluations", maxEvaluationsLearningKB);
        
        /*System.out.println("Population size.................: " + algorithm.getInputParameter("populationSize").toString());
        System.out.println("Max evaluations.................: " + algorithm.getInputParameter("maxEvaluations").toString());
        */
    
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
        double timeLearningKB = aux / 60;   // converted in minutes
        
        //System.out.println("\nTime to learning KB..: " + timeLearningKB + " minutes.");
        //System.out.println("Number of solutions..: " + population.size()); 
        
        //evaluations = ((Integer)algorithm.getOutputParameter("evaluations"));
        //System.out.println("Required evaluations.: " + evaluations);
        
        //population.printVariablesToFile("VAR_LearningKB_" + Integer.toString(indexFold));        
        //population.printObjectivesToFile("FUN_LearningKB_" + Integer.toString(indexFold));        
               
        Solution finalSolution = ((LearningKB)problem).getSolutionFromPareto(population);
        
        // accuracyTra is the accuracy on the training
        double accuracyTra = finalSolution.getObjective(0);
        
        Printer.printVariablesToFile(finalSolution, pathResult + "VAR_LearningKB_" + Integer.toString(indexFold));
        Printer.printObjectivesToFile(finalSolution, pathResult + "FUN_LearningKB_" + Integer.toString(indexFold));    
        
        //System.out.println("\nPrinting final solution...");
        //((NSGAII_LearningKB)algorithm).printOffSpring(finalSolution);      
        /* ---------------------------------------------------------------------
           End of part 2: Learning KB
        ----------------------------------------------------------------------*/ 
        
        /* ---------------------------------------------------------------------
           Part 3: Test
        ----------------------------------------------------------------------*/
        double accuracyTest = FuzzyReasoning.classicFuzzyReasoning(finalSolution, samples, partition, "test");
        
        int complexity = Rules.calcNumberOfConditions(finalSolution);
        int numberOfRules = (int)finalSolution.getDecisionVariables()[2].getValue();
        
        System.out.println("Fold = " + indexFold + " Training Accuracy = " + accuracyTra + " Test Accuracy = " + 
           accuracyTest + " Complexity = " + complexity + " #Rules = " + numberOfRules +
           " Time Select Instances = " + timeSelectInstances + 
           " Time LearningKB = " + timeLearningKB); 
        
        Printer.printResultToFile(datasetName, pathResult, indexFold, timeSelectInstances, 
           (-1.0 * reductionRate), timeLearningKB, (-1.0 * accuracyTra), 
           complexity, accuracyTest, numberOfRules);
        
        /* ---------------------------------------------------------------------
           End of part 3: Test
        ----------------------------------------------------------------------*/ 
        
    } // end of execute method

} // end of NSGAII_SelectInstances_and_LearningKB class