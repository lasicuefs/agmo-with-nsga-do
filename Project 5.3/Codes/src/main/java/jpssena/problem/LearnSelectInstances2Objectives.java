package jpssena.problem;

import org.uma.jmetal.problem.impl.AbstractBinaryProblem;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.solution.impl.DefaultBinarySolution;
import weka.classifiers.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.core.Instances;

import java.util.BitSet;

/**
 * Created by João Paulo on 13/06/2017.
 */
public class LearnSelectInstances2Objectives extends AbstractBinaryProblem {
    private Instances samples;
    private int bits;

    @Override
    public void evaluate(BinarySolution solution) {
        BitSet bitSet = solution.getVariableValue(0);
        int selected = 0;


        Instances instances = new Instances(samples);
        //for each bit (every training sample)
        for (int i = bitSet.length() - 1; i >= 0; i--) {
            if (bitSet.get(i)) {
                selected++;
            } else {
                instances.remove(i);
            }
        }
        //Selected rate calculation
        double value = (samples.numInstances() - selected) / (double)samples.numInstances();

        IBk knn = new IBk(1);

        double accuracy  = 0;

        try {
            Evaluation evaluation = new Evaluation(instances);
            knn.buildClassifier(instances);
            evaluation.evaluateModel(knn, samples);
            accuracy = evaluation.correct();
            //System.out.println("Instances: " + instances.numInstances());
            //System.out.println("Accuracy: " + accuracy);



        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

        accuracy = accuracy / samples.numInstances(); //?????? (Precisa??)


        //Multiply count -1 to minimize
        solution.setObjective(0, accuracy * -1);
        solution.setObjective(1, value * -1);
    }


    public LearnSelectInstances2Objectives(Instances samples) {
        this.samples = samples;
        this.bits = samples.numInstances();

        setName("Learn Select Instances");
        setNumberOfObjectives(2);
        setNumberOfVariables(samples.numAttributes());
    }

    @Override
    protected int getBitsPerVariable(int index) {
        return bits;
    }

    public BinarySolution createSolution() {
        return new DefaultBinarySolution(this);
    }

}
