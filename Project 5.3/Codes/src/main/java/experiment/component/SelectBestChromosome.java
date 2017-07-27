package experiment.component;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.ExperimentComponent;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;

import java.io.IOException;

/**
 * Created by João Paulo on 27/07/2017.
 */
public class SelectBestChromosome<S extends Solution<?>, Result> implements ExperimentComponent {
    private final Experiment<S, Result> experiment;

    public SelectBestChromosome(Experiment<S, Result> experiment) {
        this.experiment = experiment;
    }

    @Override
    public void run() throws IOException {
        for (ExperimentAlgorithm<?, Result> experimentAlgorithm : experiment.getAlgorithmList()) {
            String algorithmDirectory = experiment.getExperimentBaseDirectory() + "/data/" + experimentAlgorithm.getAlgorithmTag();
            String problemDirectory = algorithmDirectory + "/" + experimentAlgorithm.getProblemTag();
            String problemTag = experimentAlgorithm.getProblemTag().split("-")[0];
            String problemBase = algorithmDirectory + "/" + problemTag;


        }
    }
}
