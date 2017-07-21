package jpssena.experiments;

import util.ThreadsUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by João Paulo on 21/07/2017.
 */
public class Experiment_Learn_MultiObjective_MGPiresWay {
    private static final int INDEPENDENT_RUNS = 3;
    private static final String baseDirectory = "./dataset-test/";

    public static void main(String[] args) {
        String[] datasetNames = {"zoo"};

        String datasetLocation, configFile, pathResult, pathResultAll;
        String stratificationDataset = "10";

        int numFolds = 10;
        int numThreads = ThreadsUtil.calculateNumThreads();

        int populationSize = 100;
        int maxEvaluations = 1000;
        double probabilityCrossover = 0.9;
        double probabilityMutation  = 0.2;

        Map<Integer, Future<Object>> results = new HashMap<>();

        for (String datasetName : datasetNames) {
            datasetLocation = baseDirectory + datasetName + "/";
            configFile      = "config-" + datasetName + ".txt";
            pathResult      = "./result-Experiment_Learn_MultiObjective/" + datasetName + "/";
            pathResultAll   = "./result-Experiment_Learn_MultiObjective/";

            ExecutorService executor = Executors.newFixedThreadPool(numThreads);

            int idThread = 1;
            for (int iRun = 0; iRun < INDEPENDENT_RUNS; iRun++) {
                for (int iFold = 1; iFold < numFolds; iFold++) {
                    //Callable<Object> experiment = new
                }
            }
        }

    }
}
