package jpssena.experiments;

import jpssena.algorithm.multiobjective.NSGAIII_II;
import jpssena.experiment.component.GenerateStatistics;
import jpssena.experiment.component.SelectBestChromosome;
import jpssena.experiment.component.TestSelectedChromosome;
import jpssena.experiment.util.ExperimentAlgorithmWithTime;
import jpssena.problem.LearnMultiObjectivesSelectInstances;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaiii.NSGAIIIBuilder;
import org.uma.jmetal.operator.impl.crossover.HUXCrossover;
import org.uma.jmetal.operator.impl.mutation.BitFlipMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.ExperimentBuilder;
import org.uma.jmetal.util.experiment.component.ExecuteAlgorithms;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.util.experiment.util.ExperimentProblem;
import weka.core.Instances;

import java.io.*;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class ExperimentFindTheBest {
    private static int different = 1;
    private static final int INDEPENDENT_RUNS = 3;
    private static final String stratification = "10";
    private static final double crossoverProbability = 0.9;
    private static final double mutationProbability = 0.2;
    private static final String baseDirectory = "./testing";

    public static void main(String[] args) throws IOException {
        List<ExperimentProblem<BinarySolution>> problems = configureProblems();
        List<ExperimentAlgorithm<BinarySolution, List<BinarySolution>>> algorithms = configureAlgorithms(problems);

        Experiment<BinarySolution, List<BinarySolution>> experiment;
        experiment = new ExperimentBuilder<BinarySolution, List<BinarySolution>>("testing_stuff_car_ref_12") //Name
                .setAlgorithmList(algorithms)                                   //Algorithms created
                .setProblemList(problems)                                       //Problems created
                .setExperimentBaseDirectory(baseDirectory)                      //Directory to save results
                .setOutputParetoFrontFileName("FUN")                            //Name of the Function values file
                .setOutputParetoSetFileName("VAR")                              //Name of the Variable values file
                .setIndependentRuns(INDEPENDENT_RUNS)                           //Number of times every problem should run independently
                .setNumberOfCores(Runtime.getRuntime().availableProcessors())   //Number of Threads to Use
                .build();

        System.out.println("The experiment will start in 10 seconds.");
        System.out.println(problems.size() + " problems are going to be solved");
        System.out.println(algorithms.size() + " algorithms are going to be executed " + INDEPENDENT_RUNS + " times");

        try {
            Thread.sleep(10000);
        } catch (Exception k) {
            k.printStackTrace();
            System.exit(0);
        }
        //Executes the Experiment
        new ExecuteAlgorithms<>(experiment).run();

        for (ExperimentAlgorithm<BinarySolution, List<BinarySolution>> algorithmExp : experiment.getAlgorithmList()) {
            Algorithm<List<BinarySolution>> algorithm = algorithmExp.getAlgorithm();

            System.out.println("\n\nAlgorithm................: " + algorithm.getName());
            System.out.println("Problem..................: " + algorithmExp.getProblemTag());
            System.out.println("Tag......................: " + algorithmExp.getAlgorithmTag());
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

        System.out.println("Started: Generating Statistics");
        try {
            new GenerateStatistics<>(experiment).run();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Finished: Generating Statistics");

        System.out.println("Started: Select Best Chromosome");
        List<File> result = null;
        try {
            SelectBestChromosome<BinarySolution, List<BinarySolution>> best = new SelectBestChromosome<>(experiment, stratification);
            best.run();
            result = best.getSelectedChromosome();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Finished: Select Best Chromosome");

        System.out.println("Started: Test Selected Chromosome");
        try {
            if (result == null) {
                System.out.println("Result is null");
            } else {
                new TestSelectedChromosome<>(experiment, stratification).run();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Finished: Test Selected Chromosome");
    }

    private static List<ExperimentProblem<BinarySolution>> configureProblems () throws IOException {
        List<ExperimentProblem<BinarySolution>> problems = new ArrayList<>();

        for (int i = 1; i <=5; i++) {
            String baseName = baseDirectory + "\\australian\\" + "australian" + "-" + stratification + "-" + i;
            File training = new File(baseName + "tra.arff");
            Instances trainingInstances = new Instances(new BufferedReader(new FileReader(training)));

            if (trainingInstances.classIndex() == -1)
                trainingInstances.setClassIndex(trainingInstances.numAttributes() - 1);

            problems.add(new ExperimentProblem<>(new LearnMultiObjectivesSelectInstances(trainingInstances), "australian" + "-" + i));
        }



        for (int i = 1; i <=5; i++) {
            String baseName = baseDirectory + "\\car\\" + "car" + "-" + stratification + "-" + i;
            File training = new File(baseName + "tra.arff");
            Instances trainingInstances = new Instances(new BufferedReader(new FileReader(training)));

            if (trainingInstances.classIndex() == -1)
                trainingInstances.setClassIndex(trainingInstances.numAttributes() - 1);

            problems.add(new ExperimentProblem<>(new LearnMultiObjectivesSelectInstances(trainingInstances), "car" + "-" + i));
        }

        return problems;
    }

    private static List<ExperimentAlgorithm<BinarySolution, List<BinarySolution>>> configureAlgorithms(List<ExperimentProblem<BinarySolution>> problems) {
        List<ExperimentAlgorithm<BinarySolution, List<BinarySolution>>> algorithms = new ArrayList<>();

        int i = 1;
        for (ExperimentProblem<BinarySolution> exp_problem : problems) {
            Problem<BinarySolution> problem = exp_problem.getProblem();

            if (i == 1) {
                Algorithm<List<BinarySolution>> nsga_iii_50_50 = new NSGAIII_II<>(new NSGAIIIBuilder<>(
                        problem)
                        .setCrossoverOperator(new HUXCrossover(crossoverProbability))
                        .setMutationOperator(new BitFlipMutation(mutationProbability))
                        .setSelectionOperator(new BinaryTournamentSelection<BinarySolution>())
                        .setPopulationSize(50)
                        .setMaxIterations(50));

                algorithms.add(new ExperimentAlgorithmWithTime<BinarySolution, List<BinarySolution>>(nsga_iii_50_50, exp_problem.getTag()));
            }

            if (i == 2) {
                Algorithm<List<BinarySolution>> nsga_iii_100_50 = new NSGAIII_II<>(new NSGAIIIBuilder<>(
                        problem)
                        .setCrossoverOperator(new HUXCrossover(crossoverProbability))
                        .setMutationOperator(new BitFlipMutation(mutationProbability))
                        .setSelectionOperator(new BinaryTournamentSelection<BinarySolution>())
                        .setPopulationSize(100)
                        .setMaxIterations(50));

                algorithms.add(new ExperimentAlgorithmWithTime<BinarySolution, List<BinarySolution>>(nsga_iii_100_50, exp_problem.getTag()));
            }

            if (i == 3) {
                Algorithm<List<BinarySolution>> nsga_iii_50_100 = new NSGAIII_II<>(new NSGAIIIBuilder<>(
                        problem)
                        .setCrossoverOperator(new HUXCrossover(crossoverProbability))
                        .setMutationOperator(new BitFlipMutation(mutationProbability))
                        .setSelectionOperator(new BinaryTournamentSelection<BinarySolution>())
                        .setPopulationSize(50)
                        .setMaxIterations(100));

                algorithms.add(new ExperimentAlgorithmWithTime<BinarySolution, List<BinarySolution>>(nsga_iii_50_100, exp_problem.getTag()));
            }

            if (i == 4) {
                Algorithm<List<BinarySolution>> nsga_iii_100_100 = new NSGAIII_II<>(new NSGAIIIBuilder<>(
                        problem)
                        .setCrossoverOperator(new HUXCrossover(crossoverProbability))
                        .setMutationOperator(new BitFlipMutation(mutationProbability))
                        .setSelectionOperator(new BinaryTournamentSelection<BinarySolution>())
                        .setPopulationSize(100)
                        .setMaxIterations(100));
                algorithms.add(new ExperimentAlgorithmWithTime<BinarySolution, List<BinarySolution>>(nsga_iii_100_100, exp_problem.getTag()));
            }

            if (i == 5) {
                Algorithm<List<BinarySolution>> nsga_iii_25_100 = new NSGAIII_II<>(new NSGAIIIBuilder<>(
                        problem)
                        .setCrossoverOperator(new HUXCrossover(crossoverProbability))
                        .setMutationOperator(new BitFlipMutation(mutationProbability))
                        .setSelectionOperator(new BinaryTournamentSelection<BinarySolution>())
                        .setPopulationSize(25)
                        .setMaxIterations(100));

                algorithms.add(new ExperimentAlgorithmWithTime<BinarySolution, List<BinarySolution>>(nsga_iii_25_100, exp_problem.getTag()));
            }
            i++;
        }

        return algorithms;
    }

}
