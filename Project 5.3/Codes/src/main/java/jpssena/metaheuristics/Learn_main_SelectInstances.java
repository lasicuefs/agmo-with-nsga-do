package jpssena.metaheuristics;

import jpssena.problem.LearnSelectInstances;
import jpssena.util.ObjectIndex;
import mgpires.core.Samples;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.HUXCrossover;
import org.uma.jmetal.operator.impl.mutation.BitFlipMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalException;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

/**
 * Created by João Paulo on 15/06/2017.
 */
public class Learn_main_SelectInstances {

    public static void main(String[] args) {
        //Variables
        Problem<BinarySolution> problem;
        Algorithm<List<BinarySolution>> algorithm;
        CrossoverOperator<BinarySolution> crossoverOperator;
        MutationOperator<BinarySolution> mutationOperator;
        SelectionOperator<List<BinarySolution>, BinarySolution> selectionOperator;

        //Setup DataSet
        String dataSetName = "titanic";
        String dataSetLocation = "./dataset-test/" + dataSetName + "/";

        //Setup Samples with DataSet
        Samples samples = new Samples();
        samples.loadSamples(dataSetLocation + dataSetName, "10", 1);
        //samples.printTraSamples();
        samples.setTypeDataSet("classification");
        samples.setTypeProcedure("training");

        //Setup Problem
        problem = new LearnSelectInstances(samples);

        //Print stuff
        System.out.println("Problem name....................: " + problem.getName());
        System.out.println("Database name...................: " + dataSetName);
        System.out.println("Number of samples...............: " + samples.getNumberOfTraSamples());
        System.out.println("Number of variables.............: " + problem.getNumberOfVariables());
        System.out.println("Type of solution................: " + "BinarySolution");

        //Setup operators
        crossoverOperator = new HUXCrossover(0.9);
        mutationOperator  = new BitFlipMutation(0.2);
        selectionOperator = new BinaryTournamentSelection<>();

        //Setup the algorithm
        algorithm = new NSGAIIBuilder<>(problem, crossoverOperator, mutationOperator)
                .setPopulationSize(100)
                .setMaxEvaluations(1000)
                .setSelectionOperator(selectionOperator)
                .build();

        //Time before running
        long initTime = System.currentTimeMillis();

        //Runs the algorithm
        new AlgorithmRunner.Executor(algorithm).execute();

        //Get the result of the selection
        List<BinarySolution> solutions = algorithm.getResult();

        //Time after running
        long estimatedTime = System.currentTimeMillis() - initTime;
        double aux = estimatedTime * 0.001; // converted in seconds
        //double timeSelectInstances = aux * 0.0167;  // converted in minutes

        System.out.println("\nElapsed Time..............: " + aux + " seconds.");
        System.out.println("Number of solutions.......: " + solutions.size());

        BinarySolution finalSolution = findSolution(solutions);

        BitSet bitSet = finalSolution.getVariableValue(0);

        int count = 0;
        for (int i = 0; i < bitSet.length(); i++) {
            if (bitSet.get(i)) {
                count++;
                samples.setSelectedSamples(i, true);
            }
        }

        System.out.println("Number of Selected Samples: " + count);
        samples.setNumberOfSelectedSamples(count);

        double reduction = finalSolution.getObjective(0);
        System.out.println("Reduction Rate............: " + reduction*-1);
    }


    private static BinarySolution findSolution(List<BinarySolution> solutions) {
        int index = 0;
        if (solutions.isEmpty())
            throw new JMetalException("No solutions found");
        else if (solutions.size() <= 2)
            index = 0;
        else {
            List<ObjectIndex<Double>> doubles = new ArrayList<>();
            for (int i = 0; i < solutions.size(); i++) {
                BinarySolution solution = solutions.get(i);
                double reduction = solution.getObjective(0) * -1;
                doubles.add(new ObjectIndex<>(reduction, i));
            }

            Collections.sort(doubles);

            double min = doubles.get(0).getObject();
            double max = doubles.get(doubles.size() - 1).getObject();

            //Gets the middle value
            double mean = (min + max)/2;

            double dist = doubles.get(1).getObject() - mean;
            index = doubles.get(1).getIndex();

            for (int i = 1; i < doubles.size() - 1; i++) {
                double val = doubles.get(i).getObject() - mean;

                //The closest to the mean, the better
                if (val <= dist) {
                    dist = val;
                    index = doubles.get(i).getIndex();
                }
            }
        }

        return solutions.get(index);
    }
}
