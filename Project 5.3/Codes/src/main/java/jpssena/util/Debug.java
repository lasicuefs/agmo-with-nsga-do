package jpssena.util;

/**
 * Created by João Paulo on 19/07/2017.
 */
public class Debug {
    public static boolean DEBUG = false;
    private static boolean temp;
    public static int printLevel = 2;
    private static int currentLevel = 2;

    public static void println(String s) {
        if (DEBUG && currentLevel <= printLevel) System.out.println(s);
    }

    public static void activeTemp() {
        temp = DEBUG;
        DEBUG = true;
    }

    public static void returnTemp() {
        DEBUG = temp;
    }

    public static void setCurrentPrintLevel(int printLevel) {
        Debug.currentLevel = printLevel;
    }
}
