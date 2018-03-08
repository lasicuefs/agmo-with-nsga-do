package jpssena.experiments;

import jpssena.algorithm.multiobjective.NSGADOBuilder;
import jpssena.algorithm.multiobjective.NSGAIII_II;
import jpssena.algorithm.multiobjective.NSGAII_Alt;
import jpssena.experiment.util.ExperimentAlgorithmWithTime;
import jpssena.problem.LearnMultiObjectivesSelectInstances;
import jpssena.util.DatFixer;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.nsgaiii.NSGAIIIBuilder;
import org.uma.jmetal.operator.impl.crossover.HUXCrossover;
import org.uma.jmetal.operator.impl.mutation.BitFlipMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.ExperimentBuilder;
import org.uma.jmetal.util.experiment.component.ExecuteAlgorithms;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.util.experiment.util.ExperimentProblem;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

@SuppressWarnings("ALL")
public class ExperimentParams {
    private static final int INDEPENDENT_RUNS = 3;
    private static final String stratification = "10";
    private static final double crossoverProbability = 0.8;
    private static final double mutationProbability = 0.15;
    private static final String baseDirectory = "./testing";

    public static void main(String[] args) throws IOException {
        List<ExperimentProblem<BinarySolution>> problems = configureProblems();
        List<ExperimentAlgorithm<BinarySolution, List<BinarySolution>>> algorithms = configureAlgorithms(problems);

        Experiment<BinarySolution, List<BinarySolution>> experiment;
        experiment = new ExperimentBuilder<BinarySolution, List<BinarySolution>>("execution") //Name
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
    }

    private static List<ExperimentProblem<BinarySolution>> configureProblems() throws IOException{
        List<ExperimentProblem<BinarySolution>> problems = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            String baseName = baseDirectory + "\\australian\\" + "australian" + "-" + stratification + "-" + i;
            File training = new File(baseName + "tra.arff");
            if (!training.exists()) {
                training = DatFixer.fixDatFormat(new File(baseName + "tra.dat"));
                //training = new DatFileParser(new File(baseName + "tra.dat")).fixDatFormat();
            }

            Instances trainingInstances = new Instances(new BufferedReader(new FileReader(training)));

            if (trainingInstances.classIndex() == -1)
                trainingInstances.setClassIndex(trainingInstances.numAttributes() - 1);

            problems.add(new ExperimentProblem<>(new LearnMultiObjectivesSelectInstances(trainingInstances, "australian"), "australian" + "-" + i));
        }

        for (int i = 1; i <= 5; i++) {
            String baseName = baseDirectory + "\\car\\" + "car" + "-" + stratification + "-" + i;
            File training = new File(baseName + "tra.arff");
            if (!training.exists()) {
                training = DatFixer.fixDatFormat(new File(baseName + "tra.dat"));
                //training = new DatFileParser(new File(baseName + "tra.dat")).fixDatFormat();
            }

            Instances trainingInstances = new Instances(new BufferedReader(new FileReader(training)));

            if (trainingInstances.classIndex() == -1)
                trainingInstances.setClassIndex(trainingInstances.numAttributes() - 1);

            problems.add(new ExperimentProblem<>(new LearnMultiObjectivesSelectInstances(trainingInstances, "car"), "car" + "-" + i));
        }

        return problems;
    }

    private static List<ExperimentAlgorithm<BinarySolution, List<BinarySolution>>> configureAlgorithms(List<ExperimentProblem<BinarySolution>> problems) {
        List<ExperimentAlgorithm<BinarySolution, List<BinarySolution>>> algorithms = new ArrayList<>();

        for (ExperimentProblem<BinarySolution> exp_problem : problems) {
            Problem<BinarySolution> problem = exp_problem.getProblem();

            Algorithm<List<BinarySolution>> nsga_do = new NSGADOBuilder<>(
                    problem,                                     //The problem this algorithm is going to solve in the jpssena.experiment
                    new HUXCrossover(crossoverProbability),      //Using HUXCrossover with 0.9 probability
                    new BitFlipMutation(mutationProbability))    //Using BitFlipMutation with 0.2 probability
                    .setMaxEvaluations(0)                        //Using 0 max evaluations -> wont be used
                    .setPopulationSize(100)                      //Using a population size of 100
                    .setSelectionOperator(new BinaryTournamentSelection<BinarySolution>())
                    .build();

            //Adds this experiment algorithm to the algorithm list.
            //The ExperimentAlgorithm with time is a derivation of Experiment algorithm. The difference is that this one saves the execution time as well
            algorithms.add(new ExperimentAlgorithmWithTime<BinarySolution, List<BinarySolution>>(nsga_do, exp_problem.getTag()));


            Algorithm<List<BinarySolution>> nsga_iii = new NSGAIII_II(new NSGAIIIBuilder<>(
                    problem)
                    .setCrossoverOperator(new HUXCrossover(crossoverProbability))
                    .setMutationOperator(new BitFlipMutation(mutationProbability))
                    .setPopulationSize(100)
                    .setSelectionOperator(new BinaryTournamentSelection<BinarySolution>())
                    .setMaxIterations(0));

            algorithms.add(new ExperimentAlgorithmWithTime<BinarySolution, List<BinarySolution>>(nsga_iii, exp_problem.getTag()));

            Algorithm<List<BinarySolution>> nsga_ii = new NSGAII_Alt<>(
                    problem,                                    //The problem this algorithm is going to solve in the jpssena.experiment
                    0,
                    100,
                    new HUXCrossover(crossoverProbability),     //Using HUXCrossover with 0.9 probability
                    new BitFlipMutation(mutationProbability),   //Using BitFlipMutation with 0.2 probability
                    new BinaryTournamentSelection<BinarySolution>(new RankingAndCrowdingDistanceComparator<BinarySolution>()),
                    new SequentialSolutionListEvaluator<BinarySolution>());

            algorithms.add(new ExperimentAlgorithmWithTime<BinarySolution, List<BinarySolution>>(nsga_ii, exp_problem.getTag()));

        }

        return algorithms;
    }
}
