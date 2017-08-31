package jpssena.algorithm.util.comparator;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.RankingComparator;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by João Paulo on 30/08/2017.
 *
 * This class implements a comparator based on the rank of the solutions; if the rank is the same
 * then the crowding distance is used.
 */
public class RankingAndDistanceOrientedComparator <S extends Solution<?>> implements Comparator<S>, Serializable {
    private final Comparator<S> rankComparator = new RankingComparator<S>();

    /**
     * Compares two solutions.
     *
     * @param solution1 Object representing the first solution
     * @param solution2 Object representing the second solution.
     * @return -1, or 0, or 1 if solution1 is less than, equal, or greater than solution2,
     * respectively.
     */
    @Override
    public int compare(S solution1, S solution2) {
        int result = rankComparator.compare(solution1, solution2) ;
        if (result == 0) {
            System.out.println("Needs more magic - Selection Comparator");
            //TODO create the magic here
        }

        return result;
    }
}
