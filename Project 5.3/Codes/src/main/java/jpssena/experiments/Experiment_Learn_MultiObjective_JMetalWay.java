package jpssena.experiments;

import experiment.component.GenerateStatistics;
import experiment.component.SelectBestChromosome;
import experiment.util.ExperimentAlgorithmWithTime;
import jpssena.problem.LearnMultiObjectivesSelectInstances;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.impl.crossover.HUXCrossover;
import org.uma.jmetal.operator.impl.mutation.BitFlipMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.ExperimentBuilder;
import org.uma.jmetal.util.experiment.component.*;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.util.experiment.util.ExperimentProblem;
import util.DatFixer;
import util.Debug;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * Created by João Paulo on 19/07/2017.
 */
public class Experiment_Learn_MultiObjective_JMetalWay {
    private static final int INDEPENDENT_RUNS = 3;
    private static final int foldStart = 1;
    private static final int foldFinish = 10;
    private static final String stratification = "10";
    private static final String baseDirectory = "./dataset-test";
    private static final String[] datasetNames = {"zoo", "haberman"};

    public static void main (String[] args) {
        //Extract the List of Problems that are going to be solved;
        List<ExperimentProblem<BinarySolution>> problems = configureProblems();
        Debug.println("Number of problems to solve: " + problems.size());

        //Creates a list of algorithms that are going to solve these problems
        //Every algorithm will run every problem at least once.
        List<ExperimentAlgorithm<BinarySolution, List<BinarySolution>>> algorithms = configureAlgorithms(problems);

        //Creates the Experiment
        Experiment<BinarySolution, List<BinarySolution>> experiment;
        experiment = new ExperimentBuilder<BinarySolution, List<BinarySolution>>("The Experiment 6") //Name
                .setAlgorithmList(algorithms)                                   //Algorithms created
                .setProblemList(problems)                                       //Problems created
                .setExperimentBaseDirectory(baseDirectory)                      //Directory to save results
                .setOutputParetoFrontFileName("FUN")                            //Name of the Function values file
                .setOutputParetoSetFileName("VAR")                              //Name of the Variable values file
                .setIndependentRuns(INDEPENDENT_RUNS)                           //Number of times every problem should run independently
                .setNumberOfCores(Runtime.getRuntime().availableProcessors())   //Number of Threads to Use
                .build();

        //Executes the Experiment
        new ExecuteAlgorithms<>(experiment).run();

        //-----------------------------------------
        //This is a debugging area
        for (ExperimentAlgorithm<BinarySolution, List<BinarySolution>> algorithmExp : experiment.getAlgorithmList()) {
            Algorithm<List<BinarySolution>> algorithm = algorithmExp.getAlgorithm();

            System.out.println("\n\nAlgorithm................: " + algorithm.getName());
            System.out.println("Problem..................: " + algorithmExp.getProblemTag());
            System.out.println("Number of Solutions......: " + algorithm.getResult().size());

            for (BinarySolution solution : algorithm.getResult()) {
                System.out.println("............................................................................");
                BitSet bitSet = solution.getVariableValue(0);
                int count = 0;
                for (int i = 0; i < bitSet.length(); i++) {
                    if (bitSet.get(i)) {
                        count++;
                    }
                }
                System.out.println("Selected Samples.........: " + count);
                double reduction = solution.getObjective(1);
                System.out.println("Reduction Rate...........: " + reduction * -1);
                double accuracy = solution.getObjective(0);
                System.out.println("Accuracy Rate............: " + accuracy * -1);
            }
        }
        //-----------------------------------------

        Debug.println("Started: Generating Statistics");

        try {
            new GenerateStatistics<>(experiment).run();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Debug.println("Finished: Generating Statistics");

        Debug.println("Started: Select Best Chromosome");
        try {
            new SelectBestChromosome<>(experiment, stratification).run();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Debug.println("Finished: Select Best Chromosome");
    }

    private static List<ExperimentAlgorithm<BinarySolution, List<BinarySolution>>> configureAlgorithms(List<ExperimentProblem<BinarySolution>> problems) {
        List<ExperimentAlgorithm<BinarySolution, List<BinarySolution>>> algorithms = new ArrayList<>();

        //For every problem that are going to be solved. Create and Algorithm.
        for (ExperimentProblem<BinarySolution> exp_problem : problems) {
            Problem<BinarySolution> problem = exp_problem.getProblem();

            Algorithm<List<BinarySolution>> algorithm = new NSGAIIBuilder<>(
                    problem,                                     //The problem this algorithm is going to solve in the experiment
                    new HUXCrossover(0.9),      //Using HUXCrossover with 0.9 probability
                    new BitFlipMutation(0.2))   //Using BitFlipMutation with 0.2 probability
                    .setMaxEvaluations(1000)                     //Using 1000 max evaluations
                    .setPopulationSize(100)                      //Using a population size of 100
                    .build();

            //Adds this experiment algorithm to the algorithm list.
            //The ExperimentAlgorithm with time is a derivation of Experiment algorithm. The difference is that this one saves the execution time as well
            algorithms.add(new ExperimentAlgorithmWithTime<BinarySolution, List<BinarySolution>>(algorithm, exp_problem.getTag()));

            /*
            Algorithm<List<BinarySolution>> nsgaiii = new NSGAIIIBuilder<>(
                    problem)
                    .setCrossoverOperator(new HUXCrossover(0.9))
                    .setMutationOperator(new BitFlipMutation(0.2))
                    .setPopulationSize(100)
                    .setMaxIterations(1000)
                    .setSelectionOperator(new BinaryTournamentSelection<BinarySolution>())
                    .build();

            algorithms.add(new ExperimentAlgorithm<BinarySolution, List<BinarySolution>>(nsgaiii, exp_problem.getTag()));*/
        }

        return algorithms;
    }

    /**
     * Creates a list of BinaryProblems from a base directory.
     * @return A List of BinaryProblem
     */
    private static List<ExperimentProblem<BinarySolution>> configureProblems () {
        List<ExperimentProblem<BinarySolution>> problems = new ArrayList<>();

        File folder = new File(baseDirectory);

        if (!folder.exists() || !folder.isDirectory() || folder.listFiles() == null) {
            System.out.println("Folder doesn't exists or is empty");
        } else {
            //For each dataset name specified, go into the folder and create problems
            for (String datasetName : datasetNames) {
                File file = new File (baseDirectory + "/" + datasetName);
                if (file.isDirectory())
                    problems.addAll(createProblemsOnDirectory(file));
            }
            /*
            for (File subDirectory : folder.listFiles()) {
                if (subDirectory.isDirectory() && !subDirectory.getName().startsWith("_")) {
                    problems.addAll(createProblemsOnDirectory(subDirectory));
                }
            }
            */
        }

        return problems;
    }

    private static List<ExperimentProblem<BinarySolution>> createProblemsOnDirectory (File directory) {
        Debug.println("Analyzing Directory: " + directory.getName());
        List<ExperimentProblem<BinarySolution>> problems = new ArrayList<>();

        //Folding are almost from 1 to 10 all the time...
        for (int i = foldStart; i <= foldFinish; i++) {
            //References the Training and the Test file [Assumes it already fixed for Weka]
            String baseName = directory.getAbsolutePath() + "\\" + directory.getName() + "-" + stratification + "-" + i;
            File training   = new File(baseName + "tra.fdat");
            File testing    = new File(baseName + "tst.fdat");

            //If they don't exists it's probably because it's not fix yet.
            if (!training.exists())
                training = DatFixer.fixDatFormat(new File(baseName + "tra.dat"));
            if (!testing.exists())
                testing  = DatFixer.fixDatFormat(new File(baseName + "tst.dat"));

            Instances trainingInstances = null;
            Instances testingInstances = null;
            try {
                //Creates the Weka Instances class
                trainingInstances = new Instances(new BufferedReader(new FileReader(training)));
                testingInstances  = new Instances(new BufferedReader(new FileReader(testing)));

                //Sets the class index as the last attribute
                if (trainingInstances.classIndex() == -1)
                    trainingInstances.setClassIndex(trainingInstances.numAttributes() - 1);

                //Add this new problem to the ExperimentProblem list
                problems.add(new ExperimentProblem<>(new LearnMultiObjectivesSelectInstances(trainingInstances), directory.getName() + "-" + i));
            } catch (IOException e) {
                Debug.println("Failed to get instances for fold: " + ((trainingInstances == null) ? training.getAbsolutePath() : testing.getAbsolutePath()));
                Debug.println("Raised exception: " + e.getClass());
                Debug.println("Exception message: " + e.getMessage());
            }
        }

        return problems;
    }
}
