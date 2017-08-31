package jpssena.util;

import java.io.*;

/**
 * Created by João Paulo on 19/07/2017.
 */
public class DatFixer {

    /**
     * This method parses a .dat file from keel database so it can be used with Weka.
     * @param original The keel .dat file
     * @return The parsed file
     */
    public static File fixDatFormat(File original) {
        //Creates a file to save the modifications made
        File fixed = new File(original.getParentFile() + "\\" + original.getName().substring(0, original.getName().length() - 4) + ".arff");

        try {
            //Creates the reader for the original file and the writer for the parsed file
            BufferedWriter bw = new BufferedWriter(new FileWriter(fixed));
            BufferedReader br = new BufferedReader(new FileReader(original));
            String line;

            //Reads every line from the original file
            while ((line = br.readLine()) != null) {
                //Lines that begins with @inputs or @outputs are removed from the fixed file
                //TODO read the @output to determine the correct class. This algorithm assumes that the last attribute is always the class you looking for
                if (!line.startsWith("@inputs") && !line.startsWith("@output")) {
                    //Lines with numbers must have a correct spacing. Ex.:  @attribute name real [1, 3]
                    //Things like: @attribute name real[1,3] are not allowed
                    if (line.contains("real") || line.contains("integer")) {
                        //Finds the start position and end position of the brackets
                        int start = line.indexOf("[");
                        int end = line.indexOf("]");

                        //Splits the string extracting the value inside the brackets.
                        String split = line.substring(start + 1, end);
                        String remain = line.substring(0, start);

                        //Places a space after the real or integer name
                        if (!remain.endsWith(" "))
                            remain = remain + " ";

                        //Splits the interval into the 2 numbers
                        String[] values = split.split(",");
                        if (values.length != 2) {
                            System.err.println("It's not a distribution of 2 values!");
                            System.exit(-1);
                        }

                        //Appends to the String the values using the correct pattern
                        remain = remain + "[" + values[0] + ", " + values[1] + "]";

                        line = remain;
                    }
                    //Writes the line in the fixed file
                    bw.write(line);
                    bw.write("\n");
                }
            }

            bw.close();
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return fixed;
    }

    //Main for testing
    public static void main (String[] args) {
        fixDatFormat(new File("./dataset-test/car/car-10-2tra.dat"));
    }
}
