package jpssena.algorithm.util;

/**
 * Created by João Paulo on 26/08/2017.
 */
public class GxFunction {
    private double ak;

    public GxFunction(double ak) {
        this.ak = ak;
    }

    public double result(double x) {
        return x * Math.sqrt(1 + Math.pow(ak, 2));
    }

    public double emptyValue() {
        return Math.sqrt(1 + Math.pow(ak, 2));
    }
}
