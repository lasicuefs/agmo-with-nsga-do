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
import mgpires.core.Samples;
import mgpires.problems.SelectInstances;

public class NSGAII_main_SelectInstances {
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
        Operator  crossover ; // Crossover operator
        Operator  mutation  ; // Mutation operator
        Operator  selection ; // Selection operator    
        HashMap  parameters ; // Operator parameters
        
        Samples samples;        
      
        String datasetName     = "abalone";
        //String datasetLocation = "./dataset/" + datasetName + "/";
        String datasetLocation = "./dataset-test/" + datasetName + "/";
                
        samples = new Samples();              
        samples.loadSamples(datasetLocation + datasetName, "10", 1);  
        samples.printTraSamples();
        samples.printTestSamples();
        // There are two options: classification or regression
        samples.setTypeDataSet("classification");
        samples.setTypeProcedure("training"); 
        
        // dist gets the distance values among training samples
        //Map<String,Object> distance;       
        //distance = samples.getEuclideanDistance();
        double[][] distance;
        distance = samples.getEuclideanDistanceMatrix("train");
        
        samples.printEuclideanDistanceMatrix(distance,"train");
        
        problem = new SelectInstances("ArrayBinarySolutionType", samples, distance);
    
        // printing some data about the problem
        System.out.println("Problem name....................: " + problem.getName());
        System.out.println("Database name...................: " + datasetName);
        System.out.println("Number of variables.............: " + problem.getNumberOfVariables());
        System.out.println("Type of solution................: " + problem.getSolutionType().getClass());            
              
        algorithm = new NSGAII_SelectInstances(problem);             
 
        algorithm.setInputParameter("populationSize",100);        
        System.out.println("Population size.................: " + algorithm.getInputParameter("populationSize").toString());
        algorithm.setInputParameter("maxEvaluations",1000);    
        System.out.println("Max evaluations.................: " + algorithm.getInputParameter("maxEvaluations").toString());
    
        parameters = new HashMap();
        parameters.put("probability", 0.9);        
        crossover = CrossoverFactory.getCrossoverOperator("HUXCrossover", parameters);   
    
        parameters = new HashMap();        
        parameters.put("probability", 0.2) ;        
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
        double timeSelecInstances = aux * 0.0167;  // converted in minutes
        
        System.out.println("\nTime to select instances.: " + timeSelecInstances + " minutes.");
        System.out.println("Number of solutions......: " + population.size());
                
        int evaluations = ((Integer)algorithm.getOutputParameter("evaluations"));
        System.out.println("Required evaluations.....: " + evaluations);
                
        population.printVariablesToFile("VAR_SelectInstances");        
        population.printObjectivesToFile("FUN_SelectInstances");
        
        Solution SelectInstanceSolution = ((SelectInstances)problem).getMidPointSolutionFromPareto(population);
        double reductionRate = SelectInstanceSolution.getObjective(1);
        System.out.println("Reduction rate...........: " + (-1.0 * reductionRate));
        
        samples.printSelectedSamples();      
        
        distance = samples.getEuclideanDistanceMatrix("test");
        samples.printEuclideanDistanceMatrix(distance,"test");
        
    } // end of main method

} // end of NSGAII_main_SelectInstances class