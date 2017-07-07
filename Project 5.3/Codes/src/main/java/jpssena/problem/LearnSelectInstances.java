package jpssena.problem;

import mgpires.core.Samples;
import org.uma.jmetal.problem.impl.AbstractBinaryProblem;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.solution.impl.DefaultBinarySolution;
import org.uma.jmetal.util.JMetalException;

import java.util.BitSet;

/**
 * Created by João Paulo on 13/06/2017.
 */
public class LearnSelectInstances extends AbstractBinaryProblem {
    private Samples samples;
    private int bits;

    @Override
    public void evaluate(BinarySolution solution) {
        BitSet bitSet = solution.getVariableValue(0);
        int selected = 0;
        //for each bit (every training sample)
        for (int i = 0; i < bitSet.length(); i++)
            if (bitSet.get(i))
                selected++;

        //Selected rate calculation
        double value = (samples.getNumberOfTraSamples() - selected) / (double)samples.getNumberOfTraSamples();

        //Multiply count -1 to minimize
        solution.setObjective(0, value * -1);
    }

    public LearnSelectInstances(Samples samples) {
        this.samples = samples;
        this.bits = samples.getNumberOfTraSamples();

        setName("Learn Select Instances");
        setNumberOfObjectives(1);
        setNumberOfVariables(samples.getNumberOfVariables());
    }

    @Override
    protected int getBitsPerVariable(int index) {
        return bits;
    }


}
