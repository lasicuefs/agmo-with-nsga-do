package jpssena.util;

/**
 * Created by João Paulo on 21/07/2017.
 */
public class ThreadsUtil {
    // Calculates a adequate number of threads to process in parallel
    public static int calculateNumThreads(int numFolds) {
        int cores = Runtime.getRuntime().availableProcessors();

        int threads;
        if (numFolds <= cores) { // process all folds at the same time
            threads = numFolds;
        }
        else if (cores > numFolds / 2.0) { // balance the load in 2 batchs
            threads = (int) Math.ceil(numFolds / 2.0);
        }
        else { // use all cores to process
            threads = cores;
        }
        return threads;

    } //end calculateNumThreads method

    public static int calculateNumThreads() {
        return Runtime.getRuntime().availableProcessors();
    }
}
