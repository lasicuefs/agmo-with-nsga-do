package jpssena.experiments;

import jpssena.problem.LearnMultiObjectivesSelectInstances;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.impl.crossover.HUXCrossover;
import org.uma.jmetal.operator.impl.mutation.BitFlipMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.ExperimentBuilder;
import org.uma.jmetal.util.experiment.component.ExecuteAlgorithms;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by João Paulo on 19/07/2017.
 */
public class Experiment_Learn_MultiObjective {
    private static final int INDEPENDENT_RUNS = 25;
    private static final int foldStart = 1;
    private static final int foldFinish = 10;
    private static final String baseDirectory = "./dataset-test";

    public static void main (String[] args) {
        List<ExperimentProblem<BinarySolution>> problems = configureProblems();
        Debug.println("Number of problems to solve: " + problems.size());

        List<ExperimentAlgorithm<BinarySolution, List<BinarySolution>>> algorithms = configureAlgorithms(problems);

        Experiment<BinarySolution, List<BinarySolution>> experiment;
        experiment = new ExperimentBuilder<BinarySolution, List<BinarySolution>>("Learn Experiment")
                .setAlgorithmList(algorithms)
                .setProblemList(problems)
                .setExperimentBaseDirectory(baseDirectory)
                .setOutputParetoFrontFileName("FUN")
                .setOutputParetoSetFileName("VAR")
                .setIndependentRuns(INDEPENDENT_RUNS)
                .setNumberOfCores(Runtime.getRuntime().availableProcessors())
                .build();

        new ExecuteAlgorithms<>(experiment).run();
    }

    private static List<ExperimentAlgorithm<BinarySolution, List<BinarySolution>>> configureAlgorithms(
            List<ExperimentProblem<BinarySolution>> problems) {
        List<ExperimentAlgorithm<BinarySolution, List<BinarySolution>>> algorithms = new ArrayList<>();

        for (ExperimentProblem<BinarySolution> exp_problem : problems) {
            Problem<BinarySolution> problem = exp_problem.getProblem();

            Algorithm<List<BinarySolution>> algorithm = new NSGAIIBuilder<>(
                    problem,
                    new HUXCrossover(0.9), //I think those can be parametrized
                    new BitFlipMutation(0.2))
                    .setMaxEvaluations(1000)
                    .setPopulationSize(100)
                    .build();

            algorithms.add(new ExperimentAlgorithm<BinarySolution, List<BinarySolution>>(algorithm, exp_problem.getTag()));
        }

        return algorithms;
    }

    private static List<ExperimentProblem<BinarySolution>> configureProblems () {
        List<ExperimentProblem<BinarySolution>> problems = new ArrayList<>();

        File folder = new File(baseDirectory);

        if (!folder.exists() || !folder.isDirectory() || folder.listFiles() == null) {
            System.out.println("Folder doesn't exists or is empty");
        } else {
            for (File subDirectory : folder.listFiles()) {
                if (subDirectory.isDirectory()) {
                    problems.addAll(createProblemsOnDirectory(subDirectory));
                }
            }
        }

        return problems;
    }

    private static List<ExperimentProblem<BinarySolution>> createProblemsOnDirectory (File directory) {
        Debug.println("Analyzing Directory: " + directory.getName());
        List<ExperimentProblem<BinarySolution>> problems = new ArrayList<>();

        for (int i = foldStart; i <= foldFinish; i++) {
            String baseName = directory.getAbsolutePath() + "\\" + directory.getName() + "-" + foldFinish + "-" + i;
            File training   = new File(baseName + "tra.fdat");
            File testing    = new File(baseName + "tst.fdat");

            if (!training.exists())
                training = DatFixer.fixDatFormat(new File(baseName + "tra.dat"));
            if (!testing.exists())
                testing  = DatFixer.fixDatFormat(new File(baseName + "tst.dat"));

            Instances trainingInstances = null;
            Instances testingInstances = null;
            try {
                trainingInstances = new Instances(new BufferedReader(new FileReader(training)));
                testingInstances  = new Instances(new BufferedReader(new FileReader(testing)));

                if (trainingInstances.classIndex() == -1)
                    trainingInstances.setClassIndex(trainingInstances.numAttributes() - 1);
                problems.add(new ExperimentProblem<>(new LearnMultiObjectivesSelectInstances(trainingInstances)));
            } catch (IOException e) {
                Debug.println("Failed to get instances for fold: " + ((trainingInstances == null) ? training.getAbsolutePath() : testing.getAbsolutePath()));
                Debug.println("Raised exception: " + e.getClass());
                Debug.println("Exception message: " + e.getMessage());
            }
        }

        return problems;
    }
}
