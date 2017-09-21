package jpssena.algorithm.util.comparator;
import org.uma.jmetal.solution.Solution;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by João Paulo on 20/09/2017.
 */
public class DistanceOrientedComparator<S extends Solution<?>> implements Comparator<S>, Serializable {

    @Override
    public int compare(S solution1, S solution2) {
        double sol1 = Double.MAX_VALUE;
        if (solution1.getAttribute("IdealDistance") != null) {
            sol1 = (double)solution1.getAttribute("IdealDistance");
        }

        double sol2 = Double.MAX_VALUE;
        if (solution2.getAttribute("IdealDistance") != null) {
            sol2 = (double)solution2.getAttribute("IdealDistance");
        }

        return Double.compare(sol1, sol2);
    }
}
