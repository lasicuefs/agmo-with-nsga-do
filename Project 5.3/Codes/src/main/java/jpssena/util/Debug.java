package jpssena.util;

/**
 * Created by João Paulo on 19/07/2017.
 */
public class Debug {
    public static boolean DEBUG = true;
    private static boolean temp;

    public static void println(String s) {
        if (DEBUG) System.out.println(s);
    }

    public static void activeTemp() {
        temp = DEBUG;
        DEBUG = true;
    }

    public static void returnTemp() {
        DEBUG = temp;
    }
}
