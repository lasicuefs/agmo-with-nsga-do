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

/**
 * This component selects the best chromosome after the experiment is finished;
 * It needs the function and the variable files generated during the experiment.
 * @param <S> The Solution Type
 * @param <Result> The Result Type
 */
public class SelectBestChromosome<S extends Solution<?>, Result> implements ExperimentComponent {
    private final Experiment<S, Result> experiment;
    private final String stratification;
    private List<File> result;

    public SelectBestChromosome(Experiment<S, Result> experiment, String stratification) {
        this.experiment = experiment;
        this.stratification = stratification;
        result = new ArrayList<>();
    }

    @Override
    public void run() throws IOException {
        //For each Algorithm selected (basically every fold in every different algorithm)
        for (ExperimentAlgorithm<S, Result> experimentAlgorithm : experiment.getAlgorithmList()) {
            String algorithmDirectory = experiment.getExperimentBaseDirectory() + "/data/" + experimentAlgorithm.getAlgorithmTag();
            String problemDirectory = algorithmDirectory + "/" + experimentAlgorithm.getProblemTag();
            String problemTag = experimentAlgorithm.getProblemTag().split("-")[0];
            String problemFold = experimentAlgorithm.getProblemTag().split("-")[1];
            String problemBase = algorithmDirectory + "/" + problemTag;

            //Creates a list to hold all the info of the fold
            List<Run> functionValues = new ArrayList<>();
            List<List<Double>> allValues = new ArrayList<>();
            //For each execution of the fold
            for (int run = 0; run < experiment.getIndependentRuns(); run++) {
                //Gets the function and the variable files.
                File function = new File(problemDirectory + "/" + experiment.getOutputParetoFrontFileName() + run + ".tsv");
                File variable = new File(problemDirectory + "/" + experiment.getOutputParetoSetFileName() + run + ".tsv");

                //Gets a list of values of solutions found for the problem
                List<List<Double>> objectivesValues = GeneticUtil.functionFileToSolutionValues(function);
                //Gets all the selected solutions as BitSet
                List<BitSet> chromosome = GeneticUtil.variableFileToChromosomes(variable);

                //Put these 2 information together in a class and add it to the list
                functionValues.add(new Run(objectivesValues, chromosome));
                //Also, adds the solutions found in this run to the list
                allValues.addAll(objectivesValues);
            }

            //Based on every solution found, tries to get the mid point of all solutions
            BitSet selected = findMidPointFromPareto(functionValues, allValues);

            File baseDirectory = new File(experiment.getExperimentBaseDirectory());
            String datasets = baseDirectory.getParent();

            //Gets the training file related to this fold to serve as a model to create the reduced dataset
            File trainingFile = new File(datasets + "/" + problemTag + "/" + problemTag + "-" + stratification + "-" + problemFold + "tra.arff");
            //The Reduced dataset is marked with a red in the end of it
            File result = new File(problemBase + "/" + problemTag + "-" + stratification + "-" + problemFold + "red.arff");

            //Writes the result
            GeneticUtil.createFileWithBitSet(selected, trainingFile, result);
            //Adds this result to the list
            this.result.add(result);
        }
    }

    /**
     * The Mid point of an objective is the medium point between the maximum and the minimum value.
     * The mid point of a solution, is the mid point of every single objective.
     *
     * Thinking of the Solution as a point vector (O1, O2, O3), where O is a objective
     * If we have a (8, 1, 3) as the mid point, the best solution is the one that is closest to this point;
     *
     * The distance between two points is the difference in module of them.
     * The module of a vector is described as the square root of every element squared.
     *
     * Based on this, the solution which is closest to the mid point is the difference that gets the closest to 0
     *
     * This method is going to test all the solution and select the one that is closest to the mid point
     * @param solutions All solutions with the respective bit set
     * @param allValues All Solutions
     * @return The Selected Chromosome
     */
    private BitSet findMidPointFromPareto(List<Run> solutions, List<List<Double>> allValues) {
        int selectedRun = -1;
        int selectedSolution = -1;

        if (allValues.size() < 1)
            throw new RuntimeException("No Solutions");
        else if (allValues.size() < 2)
            return solutions.get(0).getVariable(0);

        List<Double> mid = new ArrayList<>();

        //Get the amount of objectives
        int numObjectives = allValues.get(0).size();

        //For every objective;
        for (int i = 0; i < numObjectives; i++) {
            //Gets the value into the respective solution
            List<Double> objectiveVal = new ArrayList<>();
            for (int j = 0; j < allValues.size(); j++) {
                //and then adds it to the list
                objectiveVal.add(allValues.get(j).get(i));
            }

            //Sort the objectives list
            Collections.sort(objectiveVal);

            //Gets the smallest
            double minimum = objectiveVal.get(0);
            //And the biggest value
            double maximum = objectiveVal.get(objectiveVal.size() - 1);

            //Gets the mean between the 2 of then
            double m = (minimum + maximum)/2;
            //adds it in the list
            mid.add(m);
        }

        //We want to find the smallest value, so we initialize the variable with the max value
        double minDist = Double.MAX_VALUE;

        double ideal = 0;
        for (int i = 0; i < mid.size(); i++) {
            double midPoint = mid.get(i);
            double t = Math.pow(midPoint, 2);
            ideal += t;
        }
        ideal = Math.sqrt(ideal);

        //For each run of the algorithm
        for (int run = 0; run < solutions.size(); run++) {
            Run runValue = solutions.get(run);

            //And for each solution found
            for (int sol = 0; sol < runValue.getObjectivesValues().size(); sol++) {
                //Get the value of the objectives
                List<Double> objectives = runValue.getObjectivesValues().get(sol);

                //Create a accumulator
                double value = 0;
                for (int i = 0; i < objectives.size(); i++) {
                    double objective = objectives.get(i);
                    //Power all objective by 2
                    value += Math.pow(objective, 2);
                }

                //Square root it
                double dist = Math.sqrt(value);
                //Takes the absolute in difference
                dist = Math.abs(ideal - dist);

                //Selects the smallest value close to 0
                if (dist <= minDist) {
                    selectedRun = run;
                    selectedSolution = sol;
                    minDist = dist;
                }
            }
        }

        //Return the selected chromosome
        return solutions.get(selectedRun).getVariable(selectedSolution);
    }

    public List<File> getSelectedChromosome() {
        return result;
    }

    private class Run {
        List<BitSet> variables;
        List<List<Double>> objectivesValues;

        Run(List<List<Double>> objectives, List<BitSet> variables) {
            this.variables = variables;
            this.objectivesValues = objectives;
        }

        List<List<Double>> getObjectivesValues() {
            return objectivesValues;
        }

        BitSet getVariable(int index) {
            return variables.get(index);
        }
    }
}


