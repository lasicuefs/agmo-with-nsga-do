package experiment.component;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.ExperimentComponent;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
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

    public GenerateStatistics(Experiment<S, Result> experiment) {
        this.experiment = experiment;
        directories = new Hashtable<>();
    }

    @Override
    public void run() throws IOException {
        for (ExperimentAlgorithm<?, Result> experimentAlgorithm : experiment.getAlgorithmList()) {
            String algorithmDirectory = experiment.getExperimentBaseDirectory() + "/data/" + experimentAlgorithm.getAlgorithmTag();
            String problemDirectory = algorithmDirectory + "/" + experimentAlgorithm.getProblemTag();
            String problemTag = experimentAlgorithm.getProblemTag().split("-")[0];
            String problemBase = algorithmDirectory + "/" + problemTag;

            File problemBaseDirectory = new File(problemBase);
            problemBaseDirectory.mkdirs();

            File result = new File(problemBase + "/resultsRun.txt");
            result.createNewFile();


            List<Hashtable<Integer, ObjectiveStatistics>> hashtableList = new ArrayList<>();

            for (int run = 0; run < experiment.getIndependentRuns(); run++) {
                File function = new File (problemDirectory + "/FUN" + run + ".tsv");

                Hashtable<Integer, List<Double>> objectivesPerValue = readFunctionFile(function);
                Hashtable<Integer, ObjectiveStatistics> statistics = createMeanAndSD(objectivesPerValue);
                writeStatistics(experimentAlgorithm.getProblemTag() + " " + run, statistics, result);
                hashtableList.add(statistics);
            }

            Hashtable<Integer, ObjectiveStatistics> objectiveStatistics = createSingleStatistics(hashtableList);

            List<Hashtable<Integer, ObjectiveStatistics>> problemStats = directories.get(problemBase);

            if (problemStats == null)
                problemStats = new ArrayList<>();

            problemStats.add(objectiveStatistics);
            directories.put(problemBase, problemStats);
        }

        for (String problemBase : directories.keySet()) {
            File resultFile = new File(problemBase + "/result.txt");
            resultFile.createNewFile();
            List<Hashtable<Integer, ObjectiveStatistics>> problemStats = directories.get(problemBase);
            Hashtable<Integer, ObjectiveStatistics> statisticsHashtable = createSingleStatistics(problemStats);
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

    private Hashtable<Integer, List<Double>> readFunctionFile(File function) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(function));
        String line;
        Hashtable<Integer, List<Double>> objectivesPerValue = new Hashtable<>();

        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty())
                continue;

            if (line.startsWith("Time")) {
                double time = Double.parseDouble(line.split(" ")[1]);
                List<Double> values = objectivesPerValue.get(-1);

                if (values == null)
                    values = new ArrayList<>();

                values.add(time);
                objectivesPerValue.put(-1, values);
            } else {
                String[] objectives = line.split(" ");
                for (int i = 0; i < objectives.length; i++) {
                    double value = Double.parseDouble(objectives[i]) * -1;
                    List<Double> values = objectivesPerValue.get(i);

                    if (values == null)
                        values = new ArrayList<>();

                    values.add(value);
                    objectivesPerValue.put(i, values);
                }
            }
        }

        return objectivesPerValue;
    }
}

class ObjectiveStatistics {
    double mean;
    double sd;

    ObjectiveStatistics(double mean, double sd) {
        this.mean = mean;
        this.sd = sd;
    }
}
