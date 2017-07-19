package util;

/**
 * Created by João Paulo on 19/07/2017.
 */
public class Debug {
    public static boolean DEBUG = true;

    public static void println(String s) {
        if (DEBUG) System.out.println(s);
    }
}
