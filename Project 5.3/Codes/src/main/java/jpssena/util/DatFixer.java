package jpssena.util;

import java.io.*;
import java.util.Hashtable;

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

            String lastAttributeLine = null;
            boolean processedRNG = false;
            Hashtable<Integer, String> nominalToDecimal = new Hashtable<>();

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

                    if (!line.startsWith("@relation") && !line.startsWith("@data")) {
                        if (Debug.RNG) {
                            if (line.startsWith("@attribute")) {
                                lastAttributeLine = line;
                            } else {
                                if (!processedRNG) {
                                    if (lastAttributeLine == null) {
                                        System.out.println("There's something wrong with this file..\n\t-> ORIGINAL: " + original.getName());
                                    } else {
                                        int startPosition = lastAttributeLine.indexOf("{") + 1;
                                        int endPosition = lastAttributeLine.indexOf("}");

                                        String classes = lastAttributeLine.substring(startPosition, endPosition);
                                        String[] parts = classes.split(",");
                                        for (int i = 0; i < parts.length; i++) {
                                            String part = parts[i];
                                            part = part.trim();

                                            nominalToDecimal.put(i, part);
                                        }
                                    }
                                    processedRNG = true;
                                }

                                String[] lineParts = line.split(" ");
                                String lastPart = lineParts[lineParts.length - 1];
                                int decimal = Integer.parseInt(lastPart) - 1;

                                String replacement = nominalToDecimal.get(decimal);
                                lineParts[lineParts.length - 1] = replacement;

                                StringBuilder stringBuilder = new StringBuilder();
                                for (String part : lineParts) {
                                    stringBuilder.append(part);
                                    stringBuilder.append(" ");
                                }

                                line = stringBuilder.toString().trim();
                            }

                        }
                    }

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
