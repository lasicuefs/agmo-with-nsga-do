package experiment.component;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.ExperimentComponent;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
import util.GeneticUtil;
import util.Statistics;

import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by João Paulo on 27/07/2017.
 */
public class GenerateStatistics<S extends Solution<?>, Result> implements ExperimentComponent {
    private final Experiment<S, Result> experiment;
    private Hashtable<String, List<Hashtable<Integer, ObjectiveStatistics>>> directories;
    private Hashtable<String, List<Hashtable<Integer, ObjectiveStatistics>>> means;

    public GenerateStatistics(Experiment<S, Result> experiment) {
        this.experiment = experiment;
        directories = new Hashtable<>();
        means = new Hashtable<>();
    }

    @Override
    public void run() throws IOException {
        //For each algorithm
        for (ExperimentAlgorithm<S, Result> experimentAlgorithm : experiment.getAlgorithmList()) {
            //Go to the Algorithm directory
            String algorithmDirectory = experiment.getExperimentBaseDirectory() + "/data/" + experimentAlgorithm.getAlgorithmTag();
            //Then the Problem directory
            String problemDirectory = algorithmDirectory + "/" + experimentAlgorithm.getProblemTag();
            //Get the problem tag
            String problemTag = experimentAlgorithm.getProblemTag().split("-")[0];
            //And the the problem base directory
            String problemBase = algorithmDirectory + "/" + problemTag;

            //Creates the problem base Directory
            File problemBaseDirectory = new File(problemBase);
            problemBaseDirectory.mkdirs();

            //Create a file for the results of all runs
            File result = new File(problemBase + "/resultsRun.txt");
            result.createNewFile();

            //This list contains the mean and sd of every objective of a fold
            List<Hashtable<Integer, ObjectiveStatistics>> hashtableList = new ArrayList<>();

            //For each run of fold
            for (int run = 0; run < experiment.getIndependentRuns(); run++) {
                //Gets the function file that contains the objectives values
                File function = new File (problemDirectory + "/" + experiment.getOutputParetoFrontFileName() + run + ".tsv");
                //Split the file to get the values of the objectives [here every single objective has an ID]
                Hashtable<Integer, List<Double>> objectivesPerValue = GeneticUtil.functionFileToObjectivesValue(function);
                //Gets the mean and sd of every single objective in this run
                Hashtable<Integer, ObjectiveStatistics> statistics = createMeanAndSD(objectivesPerValue);
                //Writes down the mean and SD of this run
                writeStatistics(experimentAlgorithm.getProblemTag() + " " + run, statistics, result);
                //Adds this run to the list of fold
                hashtableList.add(statistics);

                //We store all means and SD into the full thing without the folding thing;
                List<Hashtable<Integer, ObjectiveStatistics>> all = means.get(problemBase);
                if (all == null) all = new ArrayList<>();
                all.add(statistics);
                means.put(problemBase, all);
            }

            //After the we have the values of mean and sd of every run of a fold
            //we create a statistic for the hole folding
            Hashtable<Integer, ObjectiveStatistics> objectiveStatistics = createSingleStatistics(hashtableList);

            //Then, we need to add the mean and SD of the fold into the problem execution list
            //So, first we get the list of folds results
            List<Hashtable<Integer, ObjectiveStatistics>> problemStats = directories.get(problemBase);

            //If the list doesn't exists we create it
            if (problemStats == null)
                problemStats = new ArrayList<>();

            //Then we add the result of this fold into it
            problemStats.add(objectiveStatistics);
            //And we put it back into the hash table
            directories.put(problemBase, problemStats);
        }

        //For each problem we solved using this algorithm
        for (String problemBase : directories.keySet()) {
            //We create a file to store the results
            File resultFile = new File(problemBase + "/result.txt");
            resultFile.createNewFile();
            //We get the list of the result of every fold
            List<Hashtable<Integer, ObjectiveStatistics>> problemStats = means.get(problemBase);
            //Create the Mean and SD using the result of all folds
            Hashtable<Integer, ObjectiveStatistics> statisticsHashtable = createSingleStatistics(problemStats);
            //Then writes down the result
            writeStatistics("", statisticsHashtable, resultFile);
        }
    }

    private Hashtable<Integer, ObjectiveStatistics> createSingleStatistics(List<Hashtable<Integer, ObjectiveStatistics>> hashtableList) {
        Hashtable<Integer, List<Double>> objectivePerValue = new Hashtable<>();

        for (Hashtable<Integer, ObjectiveStatistics> hashtable : hashtableList) {
            for (Integer objective : hashtable.keySet()) {
                double mean = hashtable.get(objective).mean;

                List<Double> values = objectivePerValue.get(objective);
                if (values == null)
                    values = new ArrayList<>();

                values.add(mean);
                objectivePerValue.put(objective, values);
            }
        }

        Hashtable<Integer, ObjectiveStatistics> objectiveStatistics = new Hashtable<>();
        for (Integer objective : objectivePerValue.keySet()) {
            List<Double> values = objectivePerValue.get(objective);
            objectiveStatistics.put(objective, new ObjectiveStatistics(Statistics.mean(values), Statistics.sd(values)));
        }

        return objectiveStatistics;

    }

    private void writeStatistics(String header, Hashtable<Integer, ObjectiveStatistics> statistics, File result) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(result, true));
        if (!header.trim().isEmpty())
            bufferedWriter.write(header + "\t");

        for (Integer objective : statistics.keySet()) {
            ObjectiveStatistics stats = statistics.get(objective);
            bufferedWriter.write(Double.toString(stats.mean) + "\t");
            bufferedWriter.write(Double.toString(stats.sd)   + "\t");
        }
        bufferedWriter.write("\n");
        bufferedWriter.close();
    }

    private Hashtable<Integer, ObjectiveStatistics> createMeanAndSD(Hashtable<Integer, List<Double>> objectivesPerValue) {
        Hashtable<Integer, ObjectiveStatistics> statistics = new Hashtable<>();

        for (Integer i : objectivesPerValue.keySet()) {
            List<Double> list = objectivesPerValue.get(i);
            double mean = Statistics.mean(list);
            double sd   = Statistics.sd(list);
            statistics.put(i, new ObjectiveStatistics(mean, sd));
        }

        return statistics;
    }

    private class ObjectiveStatistics {
        double mean;
        double sd;

        ObjectiveStatistics(double mean, double sd) {
            this.mean = mean;
            this.sd = sd;
        }
    }
}

