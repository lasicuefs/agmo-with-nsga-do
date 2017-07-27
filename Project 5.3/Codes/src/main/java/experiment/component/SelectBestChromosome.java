package experiment.component;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.ExperimentComponent;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
import util.GeneticUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

/**
 * Created by João Paulo on 27/07/2017.
 */
public class SelectBestChromosome<S extends Solution<?>, Result> implements ExperimentComponent {
    private final Experiment<S, Result> experiment;
    private final String stratification;

    public SelectBestChromosome(Experiment<S, Result> experiment, String stratification) {
        this.experiment = experiment;
        this.stratification = stratification;
    }

    @Override
    public void run() throws IOException {
        for (ExperimentAlgorithm<S, Result> experimentAlgorithm : experiment.getAlgorithmList()) {
            String algorithmDirectory = experiment.getExperimentBaseDirectory() + "/data/" + experimentAlgorithm.getAlgorithmTag();
            String problemDirectory = algorithmDirectory + "/" + experimentAlgorithm.getProblemTag();
            String problemTag = experimentAlgorithm.getProblemTag().split("-")[0];
            String problemFold = experimentAlgorithm.getProblemTag().split("-")[1];
            String problemBase = algorithmDirectory + "/" + problemTag;

            List<Run> functionValues = new ArrayList<>();
            List<List<Double>> allValues = new ArrayList<>();
            for (int run = 0; run < experiment.getIndependentRuns(); run++) {
                File function = new File(problemDirectory + "/" + experiment.getOutputParetoFrontFileName() + run + ".tsv");
                File variable = new File(problemDirectory + "/" + experiment.getOutputParetoSetFileName() + run + ".tsv");

                List<List<Double>> objectivesValues = GeneticUtil.functionFileToSolutionValues(function);
                List<BitSet> chromosome = GeneticUtil.variableFileToChromosomes(variable);
                functionValues.add(new Run(objectivesValues, chromosome));
                allValues.addAll(objectivesValues);
            }

            BitSet selected = findMidPointFromPareto(functionValues, allValues);

            File baseDirectory = new File(experiment.getExperimentBaseDirectory());
            String datasets = baseDirectory.getParent();
            File trainingFile = new File(datasets + "/" + problemTag + "/" + problemTag + "-" + stratification + "-" + problemFold + "tra.fdat");

            File result = new File(problemBase + "/" + problemTag + "-" + stratification + "-" + problemFold + "red.dat");
            GeneticUtil.createFileWithBitSet(selected, trainingFile, result);
        }
    }

    private BitSet findMidPointFromPareto(List<Run> solutions, List<List<Double>> allValues) {
        int selectedRun = -1;
        int selectedSolution = -1;

        if (allValues.size() < 1)
            throw new RuntimeException("No Solutions");
        else if (allValues.size() < 2)
            return solutions.get(0).getVariable(0);

        List<Double> max = new ArrayList<>();
        List<Double> min = new ArrayList<>();

        for (List<Double> dList : allValues) {
            Collections.sort(dList);
            min.add(dList.get(0));
            max.add(dList.get(dList.size() - 1));
        }

        List<Double> mid = new ArrayList<>();
        for (int i = 0; i < allValues.size(); i++) {
            double m = (min.get(i) + max.get(i))/2;
            mid.add(m);
        }

        double minDist = Double.MAX_VALUE;
        for (int run = 0; run < solutions.size(); run++) {
            Run runValue = solutions.get(run);

            for (int sol = 0; sol < runValue.getObjectivesValues().size(); sol++) {
                List<Double> objectives = runValue.getObjectivesValues().get(sol);

                double value = 0;
                for (int i = 0; i < objectives.size(); i++) {
                    double d = objectives.get(i);
                    double m = mid.get(i);
                    double t = Math.pow(d - m, 2);
                    value += t;
                }

                double dist = Math.sqrt(value);

                if (dist <= minDist) {
                    selectedRun = run;
                    selectedSolution = sol;
                    minDist = dist;
                }
            }
        }

        return solutions.get(selectedRun).getVariable(selectedSolution);
    }

    private class Run {
        List<BitSet> variables;
        List<List<Double>> objectivesValues;
        int selectedIndex;

        public Run(List<List<Double>> objectives, List<BitSet> variables) {
            this.variables = variables;
            this.objectivesValues = objectives;
        }

        public List<List<Double>> getObjectivesValues() {
            return objectivesValues;
        }

        public BitSet getVariable(int index) {
            return variables.get(index);
        }
    }
}


