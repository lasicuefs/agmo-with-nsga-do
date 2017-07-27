package util;

import java.io.*;

/**
 * Created by João Paulo on 19/07/2017.
 */
public class DatFixer {

    //TODO Comment
    public static File fixDatFormat(File original) {
        File fixed = new File(original.getParentFile() + "\\" + original.getName().substring(0, original.getName().length() - 4) + ".fdat");
        System.out.println(fixed.getName());
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(fixed));
            BufferedReader br = new BufferedReader(new FileReader(original));
            String line;

            while ((line = br.readLine()) != null) {
                if (!line.startsWith("@inputs") && !line.startsWith("@output")) {
                    if (line.contains("real") || line.contains("integer")) {
                        int start = line.indexOf("[");
                        int end = line.indexOf("]");

                        String split = line.substring(start + 1, end);
                        String remain = line.substring(0, start);
                        if (!remain.endsWith(" "))
                            remain = remain + " ";


                        String[] values = split.split(",");
                        if (values.length != 2) {
                            System.err.println("It's not a distribution of 2 values!");
                            System.exit(-1);
                        }

                        remain = remain + "[" + values[0] + ", " + values[1] + "]";

                        line = remain;
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

    public static void main (String[] args) {
        fixDatFormat(new File("./dataset-test/car/car-10-2tra.dat"));
    }
}
