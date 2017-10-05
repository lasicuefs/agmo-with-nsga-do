package jpssena.algorithm.util.comparator;
import jpssena.algorithm.util.solutionattribute.IdealDistance;
import org.uma.jmetal.solution.Solution;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by João Paulo on 20/09/2017.
 */
public class DistanceOrientedComparator<S extends Solution<?>> implements Comparator<S>, Serializable {
    private IdealDistance<S> idealDistance = new IdealDistance<>();

    @Override
    public int compare(S solution1, S solution2) {
        /*double sol1 = Double.MAX_VALUE;
        if (solution1.getAttribute("IdealDistance") != null) {
            sol1 = (double)solution1.getAttribute("IdealDistance");
        }

        double sol2 = Double.MAX_VALUE;
        if (solution2.getAttribute("IdealDistance") != null) {
            sol2 = (double)solution2.getAttribute("IdealDistance");
        }

        return Double.compare(sol1, sol2);*/

        if (solution1 == null) {
            if (solution2 == null) {
                return 0;
            } else {
                return 1;
            }
        } else if (solution2 == null) {
            return -1;
        }  else {
            double distance1 = Double.MAX_VALUE;
            double distance2 = Double.MAX_VALUE;

            if (idealDistance.getAttribute(solution1) != null) {
                distance1 = idealDistance.getAttribute(solution1);
            }

            if (idealDistance.getAttribute(solution2) != null) {
                distance2 = idealDistance.getAttribute(solution2);
            }

            return Double.compare(distance1, distance2);
        }
    }
}
