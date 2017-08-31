package jpssena.experiment.util;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by João Paulo on 27/07/2017.
 *
 * Class that is used to run algorithms in a Experiment, that's why it extends Experiment Algorithm
 */
public class ExperimentAlgorithmWithTime<S extends Solution<?>, Result> extends ExperimentAlgorithm<S, Result> {

    public ExperimentAlgorithmWithTime(Algorithm<Result> algorithm, String algorithmTag, String problemTag) {
        super(algorithm, algorithmTag, problemTag);
    }

    public ExperimentAlgorithmWithTime(Algorithm<Result> algorithm, String problemTag) {
        super(algorithm, algorithm.getName(), problemTag) ;
    }

    @Override
    public void runAlgorithm(int id, Experiment<?, ?> experimentData) {
        //----------------------------------------------------------------
        //This Section is exactly equals to the Framework's code
        String outputDirectoryName = experimentData.getExperimentBaseDirectory()
                + "/data/"
                + getAlgorithmTag()
                + "/"
                + getProblemTag();

        File outputDirectory = new File(outputDirectoryName);
        if (!outputDirectory.exists()) {
            boolean result = new File(outputDirectoryName).mkdirs();
            if (result) {
                JMetalLogger.logger.info("Creating " + outputDirectoryName);
            } else {
                JMetalLogger.logger.severe("Creating " + outputDirectoryName + " failed");
            }
        }

        String funFile = outputDirectoryName + "/FUN" + id + ".tsv";
        String varFile = outputDirectoryName + "/VAR" + id + ".tsv";
        JMetalLogger.logger.info(
                " Running algorithm: " + getAlgorithmTag() +
                        ", problem: " + getProblemTag() +
                        ", run: " + id +
                        ", funFile: " + funFile);

        //Here is the new trick. Saves the time in seconds to this new variable
        long estimatedTime = new AlgorithmRunner.Executor(getAlgorithm()).execute().getComputingTime();
        double aux = estimatedTime * 0.001;
        Result population = getAlgorithm().getResult();

        new SolutionListOutput((List<S>) population)
                .setSeparator("\t")
                .setVarFileOutputContext(new DefaultFileOutputContext(varFile))
                .setFunFileOutputContext(new DefaultFileOutputContext(funFile))
                .print();
        //-------------------------------------------------------------

        //Writes the execution time in seconds to the function file.
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(funFile), true));
            bufferedWriter.write("\n\nTime: " + Double.toString(aux));
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
