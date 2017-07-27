package jpssena.experiments;

import experiment.component.GenerateStatistics;
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
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

/**
 * Created by João Paulo on 19/07/2017.
 */
public class Experiment_Learn_MultiObjective_JMetalWay {
    private static final int INDEPENDENT_RUNS = 2;
    private static final int foldStart = 1;
    private static final int foldFinish = 10;
    private static final String stratification = "10";
    private static final String baseDirectory = "./dataset-test";
    private static final String[] datasetNames = {"zoo", "haberman"};
    private static final List<String> referenceFrontFileNames = Arrays.asList("ZDT1.pf", "ZDT2.pf", "ZDT3.pf", "ZDT4.pf", "ZDT6.pf", "ZDT1.pf", "ZDT2.pf", "ZDT3.pf", "ZDT4.pf", "ZDT6.pf");

    public static void main (String[] args) {
        List<ExperimentProblem<BinarySolution>> problems = configureProblems();
        Debug.println("Number of problems to solve: " + problems.size());

        List<ExperimentAlgorithm<BinarySolution, List<BinarySolution>>> algorithms = configureAlgorithms(problems);

        Experiment<BinarySolution, List<BinarySolution>> experiment;
        experiment = new ExperimentBuilder<BinarySolution, List<BinarySolution>>("The Experiment 6")
                .setAlgorithmList(algorithms)
                .setProblemList(problems)
                .setExperimentBaseDirectory(baseDirectory)
                .setOutputParetoFrontFileName("FUN")
                //.setOutputParetoSetFileName("VAR")
                .setIndependentRuns(INDEPENDENT_RUNS)
                .setNumberOfCores(Runtime.getRuntime().availableProcessors())
                /*.setIndicatorList(Arrays.asList(
                        new Epsilon<BinarySolution>(),
                        new Spread<BinarySolution>(),
                        new GenerationalDistance<BinarySolution>(),
                        new PISAHypervolume<BinarySolution>(),
                        new InvertedGenerationalDistance<BinarySolution>(),
                        new InvertedGenerationalDistancePlus<BinarySolution>())
                )
                .setReferenceFrontDirectory("/pareto_fronts")
                .setReferenceFrontFileNames(referenceFrontFileNames)*/
                .build();

        new ExecuteAlgorithms<>(experiment).run();

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

        Debug.println("Started: Generating Statistics");

        try {
            new GenerateStatistics<>(experiment).run();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Debug.println("Finished: Generating Statistics");
/*
        try {
            new ComputeQualityIndicators<>(experiment).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
/*
        try {
            new GenerateLatexTablesWithStatistics(experiment).run();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            new GenerateWilcoxonTestTablesWithR<>(experiment).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            new GenerateFriedmanTestTables<>(experiment).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            new GenerateBoxplotsWithR<>(experiment).setRows(1).setColumns(2).setDisplayNotch().run();
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
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

    private static List<ExperimentProblem<BinarySolution>> configureProblems () {
        List<ExperimentProblem<BinarySolution>> problems = new ArrayList<>();

        File folder = new File(baseDirectory);

        if (!folder.exists() || !folder.isDirectory() || folder.listFiles() == null) {
            System.out.println("Folder doesn't exists or is empty");
        } else {
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

        for (int i = foldStart; i <= foldFinish; i++) {
            String baseName = directory.getAbsolutePath() + "\\" + directory.getName() + "-" + stratification + "-" + i;
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
