package jpssena.metaheuristics;

import jpssena.algorithm.multiobjective.NSGADOBuilder;
import jpssena.problem.LearnMultiObjectivesSelectInstances;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.crossover.HUXCrossover;
import org.uma.jmetal.operator.impl.mutation.BitFlipMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.util.AlgorithmRunner;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by João Paulo on 15/06/2017.
 */
public class Learn_MultiObjective_main_SelectInstances implements Callable {

    public Learn_MultiObjective_main_SelectInstances() {

    }

    public static void main(String[] args) throws IOException{
        //Variables
        Problem<BinarySolution> problem;
        Algorithm<List<BinarySolution>> algorithm;
        CrossoverOperator<BinarySolution> crossoverOperator;
        MutationOperator<BinarySolution> mutationOperator;

        //Setup DataSet
        String dataSetName = "zoo";
        String dataSetLocation = "./dataset-test/" + dataSetName + "/";

        String name = dataSetLocation + dataSetName + "-" + "10" + "-" + Integer.toString(1) + "tra.arff";
        BufferedReader reader = new BufferedReader(new FileReader(name));
        Instances training = new Instances(reader);

        if (training.classIndex() == -1)
            training.setClassIndex(training.numAttributes() - 1);


        //Setup Problem
        problem = new LearnMultiObjectivesSelectInstances(training);

        //Print stuff
        System.out.println("Problem name....................: " + problem.getName());
        System.out.println("Database name...................: " + dataSetName);
        System.out.println("Number of samples...............: " + training.numInstances());
        System.out.println("Number of variables.............: " + problem.getNumberOfVariables());
        System.out.println("Type of solution................: " + "BinarySolution");

        //Setup operators
        crossoverOperator = new HUXCrossover(0.9);
        mutationOperator  = new BitFlipMutation(0.2);

        //Setup the algorithm
        algorithm = new NSGADOBuilder<>(problem, crossoverOperator, mutationOperator)
                .setPopulationSize(100)
                .setMaxEvaluations(1000)
                .build();

        //Time before running
        //long initTime = System.currentTimeMillis();

        //Runs the algorithm
        long estimatedTime = new AlgorithmRunner.Executor(algorithm).execute().getComputingTime();

        //Get the result of the selection
        List<BinarySolution> solutions = algorithm.getResult();

        //Time after running
        //long estimatedTime = System.currentTimeMillis() - initTime;
        double aux = estimatedTime * 0.001; // converted in seconds
        //double timeSelectInstances = aux * 0.0167;  // converted in minutes

        System.out.println("\nElapsed Time..............: " + aux + " seconds.");
        System.out.println("Number of solutions.......: " + solutions.size());

        //BinarySolution solution = solutions.get(0);

        for (BinarySolution solution : solutions) {
            System.out.println(" ------------------------------------------------- ");
            BitSet bitSet = solution.getVariableValue(0);

            int count = 0;
            for (int i = 0; i < bitSet.length(); i++) {
                if (bitSet.get(i)) {
                    count++;
                }
            }

            System.out.println("Number of Selected Samples: " + count);
            //samples.setNumberOfSelectedSamples(count);

            double reduction = solution.getObjective(1);
            System.out.println("Reduction Rate............: " + reduction * -1);
            double accuracy = solution.getObjective(0);
            System.out.println("Accuracy Rate.............: " + accuracy * -1);
        }
    }

    @Override
    public Object call() throws Exception {
        return null;
    }
}
