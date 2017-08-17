package experiment.component;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.ExperimentComponent;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
import weka.classifiers.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.core.Instances;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Created by João Paulo on 16/08/2017.
 */
public class TestSelectedChromosome<S extends Solution<?>, Result> implements ExperimentComponent {
//    private List<File> result;
    private Experiment<S, Result> experiment;
    private final String stratification;

    public TestSelectedChromosome(Experiment<S, Result> experiment, String stratification) {
//        this.result = result;
        this.experiment = experiment;
        this.stratification = stratification;
    }

    @Override
    public void run() throws IOException {
        for (ExperimentAlgorithm<S, Result> experimentAlgorithm : experiment.getAlgorithmList()) {
            String algorithmDirectory = experiment.getExperimentBaseDirectory() + "/data/" + experimentAlgorithm.getAlgorithmTag();
            String problemTag = experimentAlgorithm.getProblemTag().split("-")[0];
            String problemFold = experimentAlgorithm.getProblemTag().split("-")[1];
            String problemBase = algorithmDirectory + "/" + problemTag;

            File fileReduced = new File(problemBase + "/" + problemTag + "-" + stratification + "-" + problemFold + "red.arff");
            File fileTest = new File(experiment.getExperimentBaseDirectory() + "/../" + problemTag + "/" + problemTag + "-" + stratification + "-" + problemFold + "tst.arff");

            Instances reduced = new Instances(new FileReader(fileReduced));
            Instances test = new Instances(new FileReader(fileTest));

            if (reduced.classIndex() == -1)
                reduced.setClassIndex(reduced.numAttributes() - 1);

            if (test.classIndex() == -1)
                test.setClassIndex(test.numAttributes() - 1);

            IBk knn = new IBk(1);
            double accuracy = 0;

            try {
                Evaluation evaluation = new Evaluation(reduced);
                knn.buildClassifier(reduced);
                evaluation.evaluateModel(knn, test);
                accuracy = evaluation.correct();
            } catch (Exception e) {
                e.printStackTrace();
            }

            accuracy = accuracy / test.numInstances();
            System.out.println("Accuracy of " + problemTag + ".F: " + problemFold + "\t " + accuracy);
        }
    }
}
