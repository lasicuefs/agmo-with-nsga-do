package util;

import java.util.List;

/**
 * Created by João Paulo on 27/07/2017.
 */
public class Statistics {

    public static double mean(List<Double> values) {
        double ac = 0;

        for (double value : values) {
            ac += value;
        }

        return ac/values.size();
    }

    public static double sd(List<Double> values) {
        if (values.size() == 1)
            return  0.0;

        double mean = mean(values);

        double ac = 0;
        for (double value : values) {
            ac += (value - mean) * (value - mean);
        }

        return Math.sqrt(ac/(values.size() - 1));
    }
}
